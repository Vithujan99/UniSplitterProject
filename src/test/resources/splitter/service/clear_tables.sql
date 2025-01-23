delete from gruppe;
delete from mitglied;
delete from rechnung;
delete from mitglied_rechnung;
ALTER TABLE gruppe ALTER COLUMN id RESTART WITH 1;