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
import com.jal.wholesales.model.LineaPedido;

import com.wholesales.exception.DataException;

public class LineaPedidoDAOImpl implements LineaPedidoDAO {
	private static Logger logger = LogManager.getLogger(LineaPedidoDAOImpl.class);

	public LineaPedidoDAOImpl() {
	}

	@Override
	public LineaPedido findById(Connection c, long id) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("id = {} ", id);
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		LineaPedido lineaPedido = null;
		try {

			// SQL
			String sql = "SELECT id, precio, unidades, total, fecha_creacion, id_pedido, id_producto"
					+ " FROM lineapedido" + " WHERE id =? ";

			logger.debug(sql);
			// create prepared statement
			preparedStatement = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			JDBCUtils.setParameter(preparedStatement, 1, id);
			rs = preparedStatement.executeQuery();

			if (rs.next()) {
				lineaPedido = loadNext(rs);
			} else {
				if (logger.isDebugEnabled())
					logger.debug("No se encontr� la linea del pedido {}", id);
			}

		} catch (SQLException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new DataException(ex);

		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return lineaPedido;
	}

	@Override
	public List<LineaPedido> findByPedido(Connection c, Long idPedido) throws DataException {

		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		List<LineaPedido> results = null;
		try {

			// SQL
			String sql = " SELECT id, precio, unidades, total, fecha_creacion, id_pedido, id_producto "

					+ " FROM LineaPedido ";

			if (logger.isDebugEnabled())
				logger.debug(sql);
			// create prepared statement
			preparedStatement = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			int i = 1;
			preparedStatement.setLong(i++, idPedido);

			rs = preparedStatement.executeQuery();

			results = new ArrayList<LineaPedido>();

			LineaPedido lineaPedido = null;
			while (rs.next()) {
				lineaPedido = loadNext(rs);
				results.add(lineaPedido);

			}

		} catch (SQLException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new DataException(ex);

		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return results;

	}

	@Override
	public LineaPedido create(Connection c, LineaPedido lineaPedido) throws DataException {

		PreparedStatement preparedStatement = null;

		ResultSet rs = null;

		try {

			// SQL

			String sql = " INSERT INTO LINEAPEDIDO( precio, unidades, total, fecha_creacion, id_pedido, id_producto) "
					+ " VALUES (?,?,?,?,?,?) ";
			if (logger.isDebugEnabled())
				logger.debug(sql);

			// create prepared statement
			preparedStatement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			int i = 1;

			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getPrecio());
			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getUnidades());
			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getTotal());
			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getFechaCreacion());
			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getIdPedido());
			JDBCUtils.setParameter(preparedStatement, i++, lineaPedido.getIdProducto());

			int insertedRows = preparedStatement.executeUpdate();
			if (insertedRows == 1) {
				rs = preparedStatement.getGeneratedKeys();
				if (rs.next()) {
					lineaPedido.setId(rs.getLong(1));
				}
			} else {
				// Lanzar una excepcion
				throw new DataException("" + lineaPedido.getId());
			}

		} catch (SQLException ex) {
			logger.warn(ex.getMessage(), ex);
			throw new DataException("" + lineaPedido.getId(), ex);
		} finally {
			JDBCUtils.close(rs);
			JDBCUtils.close(preparedStatement);

		}
		return lineaPedido;
	}

//Para eliminar las lineas del pedido tengo q recuperar el id del producto y del pedido

	private LineaPedido loadNext(ResultSet rs) throws SQLException {
		LineaPedido lineaPedido = new LineaPedido();

		int i = 1;
		// HAY QUE CAMBIAR COSAS
		lineaPedido.setId(rs.getLong(i++));
		lineaPedido.setPrecio(rs.getDouble(i++));
		lineaPedido.setUnidades(rs.getLong(i++));
		lineaPedido.setTotal(rs.getDouble(i++));
		lineaPedido.setFechaCreacion(rs.getDate(i++));
		lineaPedido.setIdPedido(rs.getLong(i++));
		lineaPedido.setIdProducto(rs.getLong(i++));
		return lineaPedido;

	}

}