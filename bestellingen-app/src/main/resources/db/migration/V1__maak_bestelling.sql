create table bestelling
(
    id            bigint generated always as identity primary key,
    klant         varchar(100)             not null,
    product       varchar(100)             not null,
    aantal        integer                  not null check (aantal > 0),
    aangemaakt_op timestamp with time zone not null
);

create index bestelling_klant_idx on bestelling (klant);
