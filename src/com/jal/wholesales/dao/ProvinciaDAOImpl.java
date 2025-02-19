package com.jal.wholesales.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jal.wholesales.dao.util.ConnectionManager;
import com.jal.wholesales.dao.util.JDBCUtils;
import com.jal.wholesales.model.Localidad;
import com.jal.wholesales.model.Provincia;
import com.wholesales.exception.DataException;
import com.wholesales.exception.InstanceNotFoundException;

public class ProvinciaDAOImpl implements ProvinciaDAO {

	private static Logger logger = LogManager.getLogger(ProvinciaDAOImpl.class);

	public Provincia findById(Connection c, long id) throws InstanceNotFoundException, DataException {

		if (logger.isDebugEnabled())
			logger.debug("id = {} ", id);

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		Provincia provincia = null;
		try {

			// SQL
			String sql = "SELECT id, nombre, id_pais" + " FROM provincia" + " WHERE id = ?";

			logger.debug(sql);
			// create prepared statement
			preparedStatement = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			JDBCUtils.setParameter(preparedStatement, 1, id);
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				provincia = loadNext(rs);
			} else {
				if (logger.isDebugEnabled())
					logger.debug("No se encontr� la provincia {}", id);

			}
		} catch (SQLException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new DataException(ex);
		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return provincia;
	}

	public List<Provincia> findByPais(Connection c, long idPais) throws DataException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		List<Provincia> results = null;
		try {

			// SQL
			String sql = "SELECT id, nombre, id_pais" + " FROM provincia ";

			if (logger.isDebugEnabled())
				logger.debug(sql);

			// create prepared statement
			preparedStatement = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			int i = 1;

			preparedStatement.setLong(i++, idPais);

			rs = preparedStatement.executeQuery();

			results = new ArrayList<Provincia>();

			Provincia provincia = null;
			while (rs.next()) {
				provincia = loadNext(rs);
				results.add(provincia);
			}

		} catch (SQLException e) {
			logger.warn(e.getMessage(), e);
		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return results;

	}

	public Provincia create(Connection c, Provincia provincia) throws DataException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;

		try {

			// SQL

			String sql = " INSERT INTO PROVINCIA( nombre, id_pais) " + " VALUES (?,?) ";
			if (logger.isDebugEnabled())
				logger.debug(sql);
			// create prepared statement
			preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			int i = 1;

			JDBCUtils.setParameter(preparedStatement, i++, provincia.getNombre());
			JDBCUtils.setParameter(preparedStatement, i++, provincia.getIdPais());

			int insertedRows = preparedStatement.executeUpdate();
			if (insertedRows == 1) {
				rs = preparedStatement.getGeneratedKeys();

			} else {
				// Lanzar una excepcion
				throw new DataException(provincia.getNombre());
			}

		} catch (SQLException e) {
			logger.warn(e.getMessage(), e);
			throw new DataException(provincia.getNombre(), e);
		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return provincia;
	}

	public void update(Connection c, Provincia provincia) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("Provincia = {}" + provincia);
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		int updatedRows = 0;
		try {

			// SQL

			String sql = " UPDATE PROVINCIA "

					+ "    	 NOMBRE = ?," + "      ID_PAIS = ?" + "  ID = ? ";
			if (logger.isDebugEnabled())
				logger.debug(sql);
			// create prepared statement
			preparedStatement = c.prepareStatement(sql);

			int i = 1;

			JDBCUtils.setParameter(preparedStatement, i++, provincia.getNombre());
			JDBCUtils.setParameter(preparedStatement, i++, provincia.getIdPais());

			updatedRows = preparedStatement.executeUpdate();
			if (updatedRows != 1) {
				throw new DataException("No se ha podido actualizar" + provincia.getId());
			}

		} catch (SQLException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new DataException("Localidad: " + provincia.getId(), ex);
		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
	}

	private Provincia loadNext(ResultSet rs) throws SQLException {
		Provincia provincia = new Provincia();

		int i = 1;
		// HAY QUE CAMBIAR COSAS
		provincia.setId(rs.getLong(i++));
		provincia.setNombre(rs.getString(i++));
		provincia.setIdPais(rs.getLong(i++));

		return provincia;

	}

}
