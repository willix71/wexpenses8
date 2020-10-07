drop table if exists WEX_Document CASCADE;
drop table if exists WEX_Expense_WEX_Document CASCADE; 
drop table if exists WEX_ExchangeRate CASCADE; 
drop table if exists WEX_Expense CASCADE;
drop table if exists WEX_ExpenseType CASCADE;
drop table if exists WEX_Payee CASCADE;
drop table if exists WEX_PayeeType CASCADE;
drop table if exists WEX_Tag CASCADE;
drop table if exists WEX_TagGroup CASCADE;
drop table if exists WEX_TagGroup_WEX_Tag CASCADE;
drop table if exists WEX_TransactionEntry CASCADE;
drop table if exists WEX_TransactionEntry_WEX_Tag CASCADE;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;
create table WEX_Document (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, documentDate date not null, fileName varchar(255) not null, primary key (id));
create table WEX_ExchangeRate (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, date date, fee double, fixFee double, fromCurrencyCode varchar(3), rate double not null, toCurrencyCode varchar(3), institution_id bigint, primary key (id));
create table WEX_Expense (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, accountingValue decimal(19,2) not null, currencyAmount decimal(19,2) not null, currencyCode varchar(3), date timestamp not null, description varchar(255), documentCount integer, externalReference varchar(255), payedDate date, exchangeRate_id bigint, expenseType_id bigint, payee_id bigint, primary key (id));
create table WEX_Expense_WEX_Document (WEX_Expense_id bigint not null, documentFiles_id bigint not null, primary key (WEX_Expense_id, documentFiles_id));
create table WEX_ExpenseType (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, displayer integer, primary key (id));
create table WEX_Payee (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, address1 varchar(255), address2 varchar(255), address3 varchar(255), city varchar(255), countryCode varchar(255), extra varchar(255), iban varchar(255), name varchar(255), postalAccount varchar(255), postalBank varchar(255), prefix varchar(255), zip varchar(255), payeeType_id bigint, primary key (id));
create table WEX_PayeeType (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, primary key (id));
create table WEX_Tag (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, number integer, type integer not null, institution_id bigint, primary key (id));
create table WEX_TagGroup (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, description varchar(255), name varchar(255), selectable boolean not null, primary key (id));
create table WEX_TagGroup_WEX_Tag (WEX_TagGroup_id bigint not null, tag_id bigint not null, primary key (WEX_TagGroup_id, tag_id));
create table WEX_TransactionEntry (id bigint not null, createdTS timestamp not null, modifiedTs timestamp, uid varchar(255) not null, version bigint, accountingBalance decimal(19,2), accountingDate date not null, accountingOrder bigint, accountingValue decimal(19,2) not null, accountingYear integer, currencyAmount decimal(19,2) not null, factor integer not null, systemEntry boolean not null, consolidationFile_id bigint, expense_id bigint, primary key (id));
create table WEX_TransactionEntry_WEX_Tag (WEX_TransactionEntry_id bigint not null, tags_id bigint not null, primary key (WEX_TransactionEntry_id, tags_id));
alter table WEX_Document add constraint UK_5yertsmd40asympx4xf6u61n2 unique (uid);
alter table WEX_Document add constraint UK_i8rw1hr01t285rh09fqbiijxj unique (fileName);
alter table WEX_ExchangeRate add constraint UK_4mrj6ivnyhq91p0qvdls90ko9 unique (uid);
alter table WEX_Expense add constraint UK_3g9xbpf6kxodops7dpbhmcwch unique (uid);
alter table WEX_ExpenseType add constraint UK_4j93kwomo5830tkcghtdt6641 unique (uid);
alter table WEX_Payee add constraint UK_9ce2vywn504fhc3733ii308jj unique (uid);
alter table WEX_PayeeType add constraint UK_3ox14v12khxi30fvhvlo8iitr unique (uid);
alter table WEX_Tag add constraint UK_tmab68c4ew3iyhlwc8rjaw21x unique (uid);
alter table WEX_TagGroup add constraint UK_59omfoixlo8cq78yr2vamddbk unique (uid);
alter table WEX_TransactionEntry add constraint UK_fuy123o2g19m1rx3fyec8fxy8 unique (uid);
alter table WEX_ExchangeRate add constraint FKfmj5ob8cqpi0fgxgmw98lblip foreign key (institution_id) references WEX_Payee;
alter table WEX_Expense add constraint FKrx7dlq1yn4jk3crjrpw5x3uuv foreign key (exchangeRate_id) references WEX_ExchangeRate;
alter table WEX_Expense add constraint FKi3umvcfbpsmi0sxh0bq9qrixq foreign key (expenseType_id) references WEX_ExpenseType;
alter table WEX_Expense add constraint FK5f6njddmg9b29df6v7sxqjwwr foreign key (payee_id) references WEX_Payee;
alter table WEX_Expense_WEX_Document add constraint FKru62r8qgt4m42tpm0vsuql1tj foreign key (documentFiles_id) references WEX_Document;
alter table WEX_Expense_WEX_Document add constraint FKchitvt7rr0gy7es1sonicxq42 foreign key (WEX_Expense_id) references WEX_Expense;
alter table WEX_Payee add constraint FK5dcf210fd8nny12ewen9hi28l foreign key (payeeType_id) references WEX_PayeeType;
alter table WEX_Tag add constraint FKlqfsdulcen6n436r9ii951699 foreign key (institution_id) references WEX_Payee;
alter table WEX_TagGroup_WEX_Tag add constraint FK99e2vsbpyx7418ghxxf70nxto foreign key (tag_id) references WEX_Tag
alter table WEX_TagGroup_WEX_Tag add constraint FKpvx2los11ygd73s1fdjep4lww foreign key (WEX_TagGroup_id) references WEX_TagGroup
alter table WEX_TransactionEntry add constraint FKael397xfdjn39qyw8n7vf8ud1 foreign key (consolidationFile_id) references WEX_Document;
alter table WEX_TransactionEntry add constraint FKlmnlduq5cyn0r5o6deuwcrseu foreign key (expense_id) references WEX_Expense on delete cascade;
alter table WEX_TransactionEntry_WEX_Tag add constraint FK8xv4ycc4ihpivm3gvywfe9u9i foreign key (tags_id) references WEX_Tag;
alter table WEX_TransactionEntry_WEX_Tag add constraint FKdwcj0kkm9qmbip0jmrtnfj7gs foreign key (WEX_TransactionEntry_id) references WEX_TransactionEntry;
drop table if exists WEX_Country CASCADE;
create table WEX_Country (countryCode varchar(2),countryName varchar(255),currencyCode varchar(3),primary key (countryCode));
insert into WEX_Country values ('CH','Switzerland','CHF');
insert into WEX_Country values ('EU','Europe','EUR');
insert into WEX_Country values ('ES','Spain','EUR');
insert into WEX_Country values ('DE','Germany','EUR');
insert into WEX_Country values ('FR','France','EUR');
insert into WEX_Country values ('IT','Italie','EUR');
insert into WEX_Country values ('NL','Netherlands','EUR');
insert into WEX_Country values ('JP','Japan','JPY');
insert into WEX_Country values ('UK','United Kingdom','GBP');
insert into WEX_Country values ('US','United States of America','USD');