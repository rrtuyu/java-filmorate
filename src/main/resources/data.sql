MERGE INTO rating (id, name, description)
VALUES (1, 'G', 'GENERAL AUDIENCES. ALL AGES ADMITTED.'),
       (2, 'PG', 'PARENTAL GUIDANCE SUGGESTED. SOME MATERIAL MAY NOT BE SUITABLE FOR CHILDREN.'),
       (3, 'PG-13', 'PARENTS STRONGLY CAUTIONED. SOME MATERIAL MAY BE INAPPROPRIATE FOR CHILDREN UNDER 13.'),
       (4, 'R', 'RESTRICTED. CHILDREN UNDER 17 REQUIRE ACCOMPANYING PARENT OR LEGAL GUARDIAN.'),
       (5, 'NC-17', 'NO ONE 17 AND UNDER ADMITTED.');

MERGE INTO genre (id, name)
VALUES  (1,'Комедия'),
        (2, 'Драма'),
        (3, 'Мультфильм'),
        (4, 'Триллер'),
        (5, 'Документальный'),
        (6, 'Боевик');