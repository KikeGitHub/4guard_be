-- V14: Add notes/observations column to wms.locations table
ALTER TABLE wms.locations ADD COLUMN IF NOT EXISTS notes TEXT;
