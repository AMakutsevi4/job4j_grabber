CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company (id, name) values (1, 'Tinkoff');
insert into company (id, name) values (2, 'Elgacoal');
insert into company (id, name) values (3, 'Gazprom');

insert into person (id, name, company_id) values (1, 'Petr', 1);
insert into person (id, name, company_id) values (2, 'Illya', 2);
insert into person (id, name, company_id) values (3, 'Sergey', 3);
insert into person (id, name, company_id) values (4, 'Vasya', 1);
insert into person (id, name, company_id) values (5, 'Alexandr', 1);
insert into person (id, name, company_id) values (6, 'Dmitry', 2);
insert into person (id, name, company_id) values (7, 'Igor', 3);
insert into person (id, name, company_id) values (8, 'Natali', 2);
insert into person (id, name, company_id) values (9, 'Slava', 2);
insert into person (id, name, company_id) values (10, 'Evgeniy', 2);
insert into person (id, name, company_id) values (11, 'Alex', 2);
insert into person (id, name, company_id) values (12, 'Tanya', 1);
insert into person (id, name, company_id) values (13, 'Ira', 1);
insert into person (id, name, company_id) values (14, 'Lena', 1);
insert into person (id, name, company_id) values (15, 'Alex', 2);

create view all_person
as select c.name as company_name, p.name as person_name
from company as c join person as p
on p.company_id = c.id
where p.company_id !=3;

select * from all_person

create view max_person_new
as select c.name as company, count(p.company_id) as max_person 
from person p join company c on c.id = p.company_id
group by c.name
having count(p.name) = (select  count(p.company_id)
as max_person from person p join company c on c.id = p.company_id
group by c.name
order by 1 desc limit 2); 

select * from max_person_new