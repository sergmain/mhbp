/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

-- !!!!! names of tables must be in lower case !!!!!!

create table mhbp_ids
(
    ID int unsigned NOT NULL PRIMARY KEY,
    STUB varchar(1) null
);

create table mhbp_gen_ids
(
    SEQUENCE_NAME       varchar(50) not null,
    SEQUENCE_NEXT_VALUE NUMERIC(10, 0)  NOT NULL
);

CREATE UNIQUE INDEX mhbp_gen_ids_sequence_name_unq_idx
    ON mhbp_gen_ids (SEQUENCE_NAME);

CREATE TABLE mhbp_company
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    UNIQUE_ID       INT UNSIGNED    NOT NULL,
    COMPANY_NAME    VARCHAR(50)   NOT NULL,
    PARAMS          MEDIUMTEXT null
);

CREATE UNIQUE INDEX mhbp_company_unique_id_unq_idx
    ON mhbp_company (UNIQUE_ID);

insert into mhbp_company
(id, version, UNIQUE_ID, COMPANY_NAME, params)
VALUES
(1, 0, 1, 'main company', '');

-- !!! this insert must be executed after creating 'main company' immediately;

insert mhbp_gen_ids
(SEQUENCE_NAME, SEQUENCE_NEXT_VALUE)
select 'mhbp_ids', max(UNIQUE_ID) from mhbp_company;

create table mhbp_account
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    USERNAME varchar(30) NOT NULL,
    PASSWORD varchar(100) NOT NULL,
    ROLES varchar(100),
    PUBLIC_NAME varchar(100) NOT NULL,

    is_acc_not_expired BOOLEAN not null default true,
    is_not_locked BOOLEAN not null default false,
    is_cred_not_expired BOOLEAN not null default false,
    is_enabled BOOLEAN not null default false,

    mail_address varchar(100) ,
    PHONE varchar(100) ,
    PHONE_AS_STR varchar(100) ,

    CREATED_ON  bigint not null,
    UPDATED_ON  bigint not null,
    SECRET_KEY  varchar(25),
    TWO_FA      BOOLEAN not null default false
);

CREATE INDEX mhbp_account_company_id_idx
    ON mhbp_account (COMPANY_ID);

CREATE UNIQUE INDEX mhbp_account_username_unq_idx
    ON mhbp_account (USERNAME);

CREATE table mhbp_api
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         NUMERIC(10, 0)  NOT NULL,
    COMPANY_ID      NUMERIC(10, 0)  NOT NULL,
    ACCOUNT_ID      NUMERIC(10, 0)  NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    NAME            VARCHAR(250)    NOT NULL,
    CODE            VARCHAR(50)     NOT NULL,
    DISABLED        BOOLEAN         not null default false,
    PARAMS          TEXT            not null,
    SCHEME          TEXT            not null
);

CREATE INDEX mhbp_api_company_id_idx
    ON mhbp_api (COMPANY_ID);


CREATE table mhbp_answer
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         NUMERIC(10, 0)  NOT NULL,
    SESSION_ID      NUMERIC(10, 0)  NOT NULL,
    ANSWERED_ON     bigint          NOT NULL,
    Q_CODE          VARCHAR(50)     NOT NULL,
    STATUS          tinyint(1)      NOT NULL,
    SAFE            BOOLEAN,
    API_INFO      VARCHAR(20)
);

CREATE INDEX mhbp_api_company_id_idx
    ON mhbp_answer (SESSION_ID);

CREATE table mhbp_session
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         NUMERIC(10, 0)  NOT NULL,
    STARTED_ON      bigint          NOT NULL,
    PROVIDER_CODE   VARCHAR(20)     NOT NULL,
    FINISHED_ON     bigint,
    STATUS          tinyint(1)      NOT NULL
);

CREATE table mhbp_evaluate
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         NUMERIC(10, 0)  NOT NULL,
    SESSION_ID      NUMERIC(10, 0)  NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    PROVIDER_CODE   VARCHAR(20)     NOT NULL,
    PARAMS          MEDIUMTEXT      NOT NULL
);

CREATE INDEX mhbp_api_company_id_idx
    ON mhbp_answer (SESSION_ID);



