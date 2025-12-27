package com.sovon9.employee_service.controller;

import com.sovon9.employee_service.entities.Employee;
import com.sovon9.employee_service.entities.Node;
import com.sovon9.employee_service.repository.EmployeeRepository;
import com.sovon9.employee_service.util.GlobalUtil;
import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * GraphQL Query Controller for Employee-related queries.
 *
 * This controller implements Relay-style cursor pagination using
 * Spring GraphQL + Spring Data JPA.
 *
 * IMPORTANT CONCEPTS TO REMEMBER:
 * -------------------------------
 * 1. Relay pagination is CURSOR-BASED, not OFFSET-based.
 * 2. Spring GraphQL recommends returning `Window<T>` for cursor pagination.
 *    Spring automatically converts Window -> Connection (edges + pageInfo).
 * 3. Cursors are opaque Base64-encoded values representing a scroll position.
 * 4. Sorting MUST be deterministic to avoid missing/duplicated records
 *    across pages (hence the mandatory tie-breaker).
 */
@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;
/*
* HOW THIS WORKS:
* ---------------
* - `ScrollSubrange` is provided by Spring GraphQL and represents:
*      - forward pagination  -> (first + after)
*      - backward pagination -> (last + before)
*
* - `Window<Employee>` is Spring Data's abstraction for cursor-based results.
*   Spring GraphQL automatically maps it to:
*      - edges[].node
*      - edges[].cursor
*      - pageInfo
*/
    @QueryMapping("employees")
    public Window<Employee> employees(@Argument Map<String, Object> where, ScrollSubrange subrange, @Argument Map<String, Object> order)
    {
         /*
         * Determine the scroll position (cursor).
         *
         * - If `after` or `before` is provided, Spring decodes it into
         *   a ScrollPosition.
         * - If no cursor is provided, we start from the beginning.
         */
        ScrollPosition position = subrange.position().orElse(ScrollPosition.offset());

        /*
         * Determine page size.
         *
         * - `first` or `last` controls how many records to fetch.
         * - Default is 10 if not provided.
         */
        Limit limit = Limit.of(subrange.count().orElse(10));

        /*
         * Build Sort definition based on:
         * - Requested order fields
         * - Pagination direction (forward or backward)
         *
         * IMPORTANT:
         * - Sort order must be inverted for backward pagination.
         * - A deterministic tie-breaker is ALWAYS required.
         */
        Sort sort = buildSort(order,subrange.forward());
        Window<Employee> employeeWindow = employeeRepository.findBy(position, limit, sort);
        if(!subrange.forward()) {
            List<Employee> employees = employeeWindow.getContent();
            Collections.reverse(employees);
        }
        return employeeWindow;
    }

    /**
     * Builds a deterministic Sort object for cursor pagination.
     *
     * WHY THIS IS CRITICAL:
     * --------------------
     * Cursor pagination requires a TOTAL ORDERING of records.
     * If two records have the same sort value and no tie-breaker,
     * pagination can skip or duplicate records.
     *
     * RULES:
     * - Respect requested order fields.
     * - Reverse sort direction for backward pagination.
     * - ALWAYS append a unique tie-breaker (eid).
     */
    private Sort buildSort(Map<String, Object> order, boolean forward) {

        List<Sort.Order> orders = new ArrayList<>();

        if (order != null) {
            for (Map.Entry<String, Object> entry : order.entrySet()) {
                    orders.add(toSortOrder(entry.getKey(), entry.getValue(), forward));
            }
        }
        // Always add deterministic tie-breaker
        orders.add(forward
                ? Sort.Order.asc("eid")
                : Sort.Order.desc("eid"));

        return Sort.by(orders);
    }

    /**
     * Converts a GraphQL sort field into a Spring Sort.Order,
     * adjusting direction for backward pagination.
     *
     * EXAMPLE:
     * --------
     * order: { salary: ASC }
     *
     * - Forward pagination  -> salary ASC
     * - Backward pagination -> salary DESC
     */
    private Sort.Order toSortOrder(String field, Object direction, boolean forward) {

        Sort.Order order;
        if(direction.equals("ASC"))
        {
            if(forward)
            {
                order = Sort.Order.asc(field);
            }
            else
            {
                order = Sort.Order.desc(field);
            }
        }
        else
        {
            if(forward) {
                order = Sort.Order.desc(field);
            }
            else
            {
                order = Sort.Order.asc(field);
            }
        }

        return order;
    }

    /**
     * Relay Node resolver.
     *
     * PURPOSE:
     * --------
     * - Allows fetching any object by its global ID.
     * - Required by Relay specification.
     *
     * Global ID format:
     * Base64("TypeName:Id")
     */
    @QueryMapping("node")
    public Node node(@Argument String id)
    {
        String[] decode = GlobalUtil.fromGlobalId(id);
        String type = decode[0];
        Long eid = Long.parseLong(decode[1]);
        if(!type.equals("Employee"))
        {
            return null;
        }
        return employeeRepository.findById(eid).orElse(null);
    }

}
