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

-- Common for Metaheuristic DDLs, must the same as for Metaheuristic

create table mh_ids
(
    ID int unsigned NOT NULL PRIMARY KEY,
    STUB varchar(1) null
);

create table mh_gen_ids
(
    SEQUENCE_NAME       varchar(50) not null,
    SEQUENCE_NEXT_VALUE NUMERIC(10, 0)  NOT NULL
);

CREATE UNIQUE INDEX mh_gen_ids_sequence_name_unq_idx
    ON mh_gen_ids (SEQUENCE_NAME);

CREATE TABLE mh_company
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    UNIQUE_ID       INT UNSIGNED    NOT NULL,
    NAME            VARCHAR(50)   NOT NULL,
    PARAMS          MEDIUMTEXT null
);

CREATE UNIQUE INDEX mh_company_unique_id_unq_idx
    ON mh_company (UNIQUE_ID);

insert into mh_company
(id, version, UNIQUE_ID, NAME, params)
VALUES
(1, 0, 1, 'Main company', '');

-- !!! this insert must be executed after creating 'main company' immediately;

insert mh_gen_ids
(SEQUENCE_NAME, SEQUENCE_NEXT_VALUE)
select 'mh_ids', max(UNIQUE_ID) from mh_company;

create table mh_account
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    USERNAME        varchar(30) NOT NULL,
    PASSWORD        varchar(100) NOT NULL,
    ROLES           varchar(100),
    PUBLIC_NAME     varchar(100) NOT NULL,

    is_acc_not_expired BOOLEAN not null default true,
    is_not_locked   BOOLEAN not null default false,
    is_cred_not_expired BOOLEAN not null default false,
    is_enabled      BOOLEAN not null default false,

    mail_address    varchar(100) ,
    PHONE           varchar(100) ,
    PHONE_AS_STR    varchar(100) ,

    CREATED_ON      bigint not null,
    UPDATED_ON      bigint not null,
    SECRET_KEY      varchar(25),
    TWO_FA          BOOLEAN not null default false
);

CREATE INDEX mh_account_company_id_idx
    ON mh_account (COMPANY_ID);

CREATE UNIQUE INDEX mh_account_username_unq_idx
    ON mh_account (USERNAME);

-- Specific for MHPB DDLs

CREATE table mhbp_auth
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    CODE            VARCHAR(50)     NOT NULL,
    DISABLED        BOOLEAN         not null default false,
    PARAMS          TEXT            not null
);

CREATE INDEX mhbp_auth_company_id_idx
    ON mhbp_auth (COMPANY_ID);

CREATE table mhbp_api
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    NAME            VARCHAR(250)    NOT NULL,
    CODE            VARCHAR(50)     NOT NULL,
    DISABLED        BOOLEAN         not null default false,
    SCHEME          TEXT            not null
);

CREATE INDEX mhbp_api_company_id_idx
    ON mhbp_api (COMPANY_ID);

CREATE table mhbp_kb
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    CODE            VARCHAR(50)     NOT NULL,
    DISABLED        BOOLEAN         not null default false,
    PARAMS          TEXT            not null,
    STATUS          tinyint(1)      NOT NULL default 0
);

CREATE INDEX mhbp_kb_company_id_idx
    ON mhbp_kb (COMPANY_ID);

CREATE table mhbp_chapter
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    KB_ID           INT UNSIGNED    NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    CODE            VARCHAR(100)    NOT NULL,
    DISABLED        BOOLEAN         not null default false,
    PARAMS          TEXT            not null,
    STATUS          tinyint(1)      NOT NULL default 0
);

CREATE UNIQUE INDEX mhbp_chapter_kb_id_code_idx
    ON mhbp_chapter (KB_ID, CODE);

CREATE table mhbp_answer
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    SESSION_ID      INT UNSIGNED  NOT NULL,
    CHAPTER_ID      INT UNSIGNED  NOT NULL,
    ANSWERED_ON     bigint          NOT NULL,
    Q_CODE          VARCHAR(50)     NOT NULL,
    STATUS          tinyint(1)      NOT NULL,
    PARAMS          TEXT            not null,
    TOTAL           int             not null,
    FAILED          int             not null,
    SYSTEM_ERROR    int             not null
);

CREATE INDEX mhbp_answer_company_id_idx
    ON mhbp_answer (SESSION_ID);

CREATE table mhbp_session
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    EVALUATION_ID   INT UNSIGNED    NOT NULL,
    STARTED_ON      bigint          NOT NULL,
    PROVIDER_CODE   VARCHAR(50)     NOT NULL,
    FINISHED_ON     bigint,
    STATUS          tinyint         NOT NULL
);

CREATE INDEX mhbp_session_company_id_idx
    ON mhbp_session (COMPANY_ID);

CREATE table mhbp_evaluation
(
    ID              INT UNSIGNED    NOT NULL AUTO_INCREMENT  PRIMARY KEY,
    VERSION         INT UNSIGNED    NOT NULL,
    COMPANY_ID      INT UNSIGNED    NOT NULL,
    ACCOUNT_ID      INT UNSIGNED    NOT NULL,
    API_ID          INT UNSIGNED    NOT NULL,
    CHAPTER_IDS     VARCHAR(2048)   NOT NULL,
    CREATED_ON      bigint          NOT NULL,
    CODE            VARCHAR(50)     NOT NULL
);

CREATE INDEX mhbp_evaluation_company_id_idx
    ON mhbp_evaluation (COMPANY_ID);



