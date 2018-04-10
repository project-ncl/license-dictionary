--
-- JBoss, Home of Professional Open Source.
-- Copyright 2017 Red Hat, Inc., and individual contributors
-- as indicated by the @author tags.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--


--
INSERT INTO license_approval_status VALUES (nextval('license_apprstatus_id_seq'), 'APPROVED');
INSERT INTO license_approval_status VALUES (nextval('license_apprstatus_id_seq'), 'NOT_APPROVED');
INSERT INTO license_approval_status VALUES (nextval('license_apprstatus_id_seq'), 'UNKNOWN');

--
INSERT INTO license_hint_type VALUES (nextval('license_hinttype_id_seq'), 'license file');
INSERT INTO license_hint_type VALUES (nextval('license_hinttype_id_seq'), 'pom file');
INSERT INTO license_hint_type VALUES (nextval('license_hinttype_id_seq'), 'readme file');
INSERT INTO license_hint_type VALUES (nextval('license_hinttype_id_seq'), 'package.json file');
INSERT INTO license_hint_type VALUES (nextval('license_hinttype_id_seq'), 'npm-shrinkwrap.json file');

--
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'mvn');
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'npm');
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'nuget');
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'pypi');
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'gem');
INSERT INTO project_ecosystem VALUES (nextval('project_ecosystem_id_seq'), 'github');

--
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file only', 'Project licenses are specified in license file, pom.xml is missing license tag, project does not have readme file or licenses are not mentioned in readme');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file and pom', 'Project licenses are specified in license file, pom.xml contains license tag listing the same licenses, project does not have readme file or licenses are not mentioned in readme');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file with wrong licenses in pom', 'Project licenses are specified in license file, pom.xml contains license tag listing different licenses, project does not have readme file or licenses are not mentioned in readme');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, license part of readme', 'Project does not contain license file, pom.xml does not contain license tag listing different licenses, project has readme where are specified licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file, pom, part of readme', 'Project licenses are specified in license file, pom.xml contains license tag listing the same licenses, project has readme file listing the same licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, license part of readme and pom file', 'Project does not contain license file, pom.xml contains license tag listing licenses, project has readme file listing the same licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, part of readme with wrong licenses in pom file', 'Project does not contain license file, project has readme file listing the licenses, but pom.xml contains different licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file, pom file, wrong in readme file', 'Project licenses are specified in license file, pom.xml contains license tag listing the same licenses, project has readme file with different licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file, wrong pom file, wrong in readme file', 'Project licenses are specified in license file, pom.xml contains license tag listing different licenses, project has readme file with different licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, not in readme file, only in pom file', 'Project is missing license file, readme file does not contain licenses, pom.xml contains license tag listing licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, missing readme file, only in pom file', 'Project is missing license file and readme file, pom.xml contains license tag listing licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'Missing license file, not in readme file, not in pom file', 'Project is missing license file and readme file, pom.xml does not contain license tag');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file, readme file, wrong licenses in pom', 'Project licenses are specified in license file and mentioned in readme file, but pom.xml contains licenses tag listing different licenses');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'License file, readme file, missing in pom file', 'Project licenses are specified in license file and mentioned in readme file, pom.xml does not contain licenses tag');
INSERT INTO license_determination_type(id, name, description) VALUES (nextval('license_dettype_id_seq'), 'No license file, no readme or without license, only pom file without licenses tag', 'Project does not contain license file, license is not specified in readme or readme is not present and project consists of a pom file and no actual source code files. Can be ignored or marked as having Public Domain license');


