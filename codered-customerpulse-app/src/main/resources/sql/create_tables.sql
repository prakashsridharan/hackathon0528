CREATE TABLE input_data_request
( input_data_request_oid number(10) NOT NULL,
  timestamp Date NOT NULL,
  input_text varchar2(4000),  
  output_response varchar2(30),
  CONSTRAINT input_data_request_pk PRIMARY KEY (input_data_request_oid)
);

drop table input_parsed_data;

CREATE TABLE input_parsed_data
( input_parsed_data_oid number(10) NOT NULL,
  input_data_request_oid number(10) NOT NULL,
  sentence_number NUMBER(10),
  pos_tag varchar2(10),
  token_string varchar2(1000),
  rank number(6),
  negation char(1) default 'N',
  tense NUMBER(1),
  CONSTRAINT input_parsed_data_pk PRIMARY KEY (input_parsed_data_oid),
  CONSTRAINT fk_input_data_request
    FOREIGN KEY (input_data_request_oid)
    REFERENCES input_data_request(input_data_request_oid)
);

CREATE TABLE lookup_words
( word varchar2(1000),
  rank NUMBER(10)
  };