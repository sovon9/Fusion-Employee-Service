create table department(id bigint primary key not null unique, name varchar(10), status varchar(10));
create table employee(id bigint primary key not null unique, name varchar(20), salary double, dept_id bigint, foreign key(dept_id) references department(id));

INSERT INTO department (id, name, status) VALUES
(1, 'IT', 'active'),
(2, 'NET', 'active');

INSERT INTO employee (id, name, salary, dept_id) VALUES
(1000, 'Sovon',   150000, 1),
(1001, 'Sougata', 100000, 1),
(1002, 'Adam',     50000, 2),
(1003, 'Apas',     80000, 2);
(1004, 'Alan',     100000, 1);
