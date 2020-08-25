create sequence hibernate_sequence start with 1 increment by 1
create table expense2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, accounting_value decimal(19,2) not null, currency_amount decimal(19,2) not null, currency_code varchar(3), date timestamp not null, description varchar(255), external_reference varchar(255), exchange_rate_id bigint, expense_type_id bigint, payee_id bigint, primary key (id))
create table expense_rate2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, date date, fee double, fix_fee double, from_currency_code varchar(3), rate double not null, to_currency_code varchar(3), institution_id bigint, primary key (id))
create table expense_type2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, primary key (id))
create table payee2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, address1 varchar(255), address2 varchar(255), address3 varchar(255), city varchar(255), country_code varchar(255), extra varchar(255), iban varchar(255), name varchar(255), postal_account varchar(255), postal_bank varchar(255), prefix varchar(255), zip varchar(255), payee_type_id bigint, primary key (id))
create table payee_type2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, primary key (id))
create table tag2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, number integer, type integer not null, primary key (id))
create table transaction_entry2 (id bigint not null, createdts timestamp not null, modified_ts timestamp, uid varchar(255) not null, version bigint, accounting_balance decimal(19,2), accounting_order bigint, accounting_value decimal(19,2) not null, accounting_year integer, currency_amount decimal(19,2) not null, factor integer not null, system_entry boolean not null, expense_id bigint, primary key (id))
create table transaction_entry2_tags (transaction_entry_id bigint not null, tags_id bigint not null, primary key (transaction_entry_id, tags_id))

alter table expense2 add constraint UK_luqx916ttgq37owgt8yw7ysh9 unique (uid)
alter table expense_rate2 add constraint UK_nb4gc6dngxgxhhvjqeixae380 unique (uid)
alter table expense_type2 add constraint UK_1fn7l4ioho2wm3jmhejcy1vx6 unique (uid)
alter table payee2 add constraint UK_4dqneaelf32u7w6si1brpxvig unique (uid)
alter table payee_type2 add constraint UK_8yxedtdotcyq4gfr057mp308s unique (uid)
alter table tag2 add constraint UK_ik8c2ppbqsm4gtmmbnsljd6uv unique (uid)
alter table transaction_entry2 add constraint UK_hyo7itnmptlqvoqydr4je8ba8 unique (uid)
alter table expense2 add constraint FK9np0vypbcqjwa68gkylyucw2k foreign key (exchange_rate_id) references expense_rate2
alter table expense2 add constraint FKih0hlbequjkrv34jp4wffs8q2 foreign key (expense_type_id) references expense_type2
alter table expense2 add constraint FK7kodweo4bnflpwd13egkglh9w foreign key (payee_id) references payee2
alter table expense_rate2 add constraint FKc3leno2bpwwxx91f5bareyw7m foreign key (institution_id) references payee2
alter table payee2 add constraint FK1tjdanglwhxxuj2bjyqbo117o foreign key (payee_type_id) references payee_type2
alter table transaction_entry2 add constraint FK9dakxa9ul7y662esfuv6xvdlj foreign key (expense_id) references expense2 on delete cascade
alter table transaction_entry2_tags add constraint FKkrm6tbgojsrevfhqp37fayv7c foreign key (tags_id) references tag2
alter table transaction_entry2_tags add constraint FK7vwluaxlb6x87cnxko94ioqf6 foreign key (transaction_entry_id) references transaction_entry2
