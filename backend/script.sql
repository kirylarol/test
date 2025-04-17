create database SpendSculptor;
\connect spendsculptor;

create table if not exists account
(
    account_id serial not null
        primary key,
    created_at date,
    name       varchar(255)
);

create table if not exists categories
(
    category_id   serial not null
        primary key,
    category_name varchar(255)
        unique
);

create table if not exists goal
(
    account_id    integer
        constraint fks1q7s4ihcfh4qt3lk128f7p8p
            references account,
    created_at    date,
    current_state numeric(38, 2),
    goal_id       integer not null
        primary key,
    goal_value    numeric(38, 2),
    valid_until   date
);

create table if not exists identity
(
    identity_id serial not null
        primary key,
    name        varchar(255),
    surname     varchar(255)
);

create table if not exists shop
(
    shop_id serial not null
        primary key,
    name    varchar(255)
);

create table if not exists receipts
(
    account_id   integer
        constraint fk75brkfe4mf5cohwesbscp88y2
            references account,
    date         date,
    receipt_id   serial not null
        primary key,
    shop_id      integer
        constraint fkdpbn38i1mj0vpnna2htnaft02
            references shop,
    total_amount numeric(38, 2)
);

create table if not exists positions
(
    category_id serial
        constraint fkhiv1071gfrp4j95xo82wn1aj4
            references categories,
    position_id integer not null
        primary key,
    price       numeric(38, 2),
    receipt_id  integer
        constraint fklostnkepde8gscade1vrd7qv
            references receipts,
    name        varchar(255)
);

create table if not exists userprofile
(
    identity_id integer
        unique
        constraint fkg4n9r6mqo9xfytrlymw85ctas
            references identity,
    user_id     serial not null
        primary key,
    login       varchar(255),
    password    varchar(255),
    role        varchar(255)
        constraint userprofile_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ROLE_USER'::character varying, 'ROLE_ADMIN'::character varying])::text[]))
);

create table if not exists account_to_user
(
    account_id integer
        constraint fkiag0huw3lnwdxxucl9ti7ovb5
            references account,
    id         serial not null
        primary key,
    user_id    integer
        constraint fkmag8mm519k7ne4ms2otibet30
            references userprofile,
    weight     double precision,
    permission varchar(255)
        constraint account_to_user_permission_check
            check ((permission)::text = ANY
                   ((ARRAY ['ACCOUNT_CREATOR'::character varying, 'ACCOUNT_ADMIN'::character varying, 'ACCOUNT_USER'::character varying])::text[]))
);

CREATE SEQUENCE account_seq START 1;
CREATE SEQUENCE categories_seq START 1;
CREATE SEQUENCE goal_seq START 1;
CREATE SEQUENCE identity_seq START 1;
CREATE SEQUENCE shop_seq START 1;
CREATE SEQUENCE receipts_seq START 1;
CREATE SEQUENCE positions_seq START 1;
CREATE SEQUENCE userprofile_seq START 1;
CREATE SEQUENCE account_to_user_seq START 1;

