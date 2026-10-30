package br.com.EcoPulse.repository.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class JdbcUtils {
    private JdbcUtils() { }

    public static void setInstant(PreparedStatement ps, int index, Instant value) throws SQLException {
        if (value == null) ps.setNull(index, Types.TIMESTAMP_WITH_TIMEZONE);
        else ps.setObject(index, OffsetDateTime.ofInstant(value, ZoneOffset.UTC));
    }

    public static Instant getInstant(ResultSet rs, String column) throws SQLException {
        try {
            OffsetDateTime odt = rs.getObject(column, OffsetDateTime.class);
            return odt == null ? null : odt.toInstant();
        } catch (SQLException ex) {
            Timestamp ts = rs.getTimestamp(column);
            return ts == null ? null : ts.toInstant();
        }
    }

    public static void setLocalDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
        if (value == null) ps.setNull(index, Types.DATE);
        else ps.setDate(index, Date.valueOf(value));
    }

    public static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        Date d = rs.getDate(column);
        return d == null ? null : d.toLocalDate();
    }

    public static void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) ps.setNull(index, Types.NUMERIC);
        else ps.setLong(index, value);
    }

    public static Long getNullableLong(ResultSet rs, String column) throws SQLException {
        Number n = (Number) rs.getObject(column);
        return n == null ? null : n.longValue();
    }

    public static long extractGeneratedId(PreparedStatement ps) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) return keys.getLong(1);
            throw new SQLException("Chave primária gerada não foi localizada");
        }
    }
}
