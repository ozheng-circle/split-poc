/*
 * Copyright (c) 2023, Circle Internet Financial Trading Company Limited.
 * All rights reserved.
 *
 * Circle Internet Financial Trading Company Limited CONFIDENTIAL
 * This file includes unpublished proprietary source code of Circle Internet
 * Financial Trading Company Limited, Inc. The copyright notice above does not
 * evidence any actual or intended publication of such source code. Disclosure
 * of this source code or any related proprietary information is strictly
 * prohibited without the express written permission of Circle Internet Financial
 * Trading Company Limited.
 */

SET search_path=public;

CREATE SCHEMA IF NOT EXISTS ${schema};

SET search_path=${schema},public;

-- ----------------------
-- Create ${dbuser}
-- ----------------------
DO $$
BEGIN
  IF NOT EXISTS(
      SELECT *
      FROM pg_catalog.pg_user
      WHERE usename = '${dbuser}')
  THEN
    CREATE ROLE ${dbuser} LOGIN PASSWORD '${database_password}';
  END IF;
END
$$;

ALTER USER ${dbuser} SET search_path TO '${schema}';

-- ---------------------------------------------------------------
-- Create pi_ro_dbuser
-- ---------------------------------------------------------------
DO $$
BEGIN
  IF NOT EXISTS(
      SELECT *
      FROM pg_catalog.pg_user
      WHERE usename = 'pi_ro_dbuser')
  THEN
    CREATE ROLE pi_ro_dbuser LOGIN PASSWORD '${pi_ro_database_password}';
  END IF;
END
$$;

-- --------
-- Grants
-- --------
GRANT USAGE ON SCHEMA ${schema} TO ${dbuser}, pi_ro_dbuser;
