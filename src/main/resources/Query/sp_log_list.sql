CREATE  PROCEDURE `sp_get_sensor_history`(
    IN p_from DATETIME,
    IN p_to DATETIME,
    IN p_page INT,
    IN p_size INT
)
BEGIN
    DECLARE v_offset INT DEFAULT 0;
    DECLARE v_total BIGINT DEFAULT 0;

    SET v_offset = p_page * p_size;

    SELECT COUNT(DISTINCT recorded_at)
    INTO v_total
    FROM tb_sensor_reading
    WHERE recorded_at BETWEEN p_from AND p_to;

    SELECT
        v_total AS total,
        UNIX_TIMESTAMP(r.recorded_at)*1000 AS time,
        MAX(CASE WHEN s.type = 'TEMPERATURE' THEN r.value END) AS temp,
        MAX(CASE WHEN s.type = 'HUMIDITY' THEN r.value END) AS humidity,
        MAX(CASE WHEN s.type = 'SOIL_MOISTURE' THEN r.value END) AS soil,
        MAX(CASE WHEN s.type = 'LIGHT' THEN r.value END) AS light,
        DATE(r.recorded_at) AS date
    FROM tb_sensor_reading r
    JOIN tb_sensor s ON s.id = r.sensor_id
    WHERE r.recorded_at BETWEEN p_from AND p_to
    GROUP BY r.recorded_at
    ORDER BY r.recorded_at DESC
    LIMIT p_size OFFSET v_offset;
END