import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

//----------------------------------------------------------
// jdbc query executor - Groovy script  
//----------------------------------------------------------
// Executes database queries using datasource or direct jdbc connection
//	- please set "useDataSource" to true in order to use jndi to get connection from dataSource.
//
//
	boolean useDataSource = false;
	String driverClassName = "com.mysql.jdbc.Driver";
	String jdbcConnectionUrl = "jdbc:mysql://localhost:3306/lfr70_hc15372";
	String jndiName= "";
	String user = "root";
	String pass = "";
	String queryToExecute = "select userId,screenname from user_";

// Tested with Liferay 7.0 CE / DXP
// author: Simone Cinti - 04/06/2020
//----------------------------------------------------------

try { 
	JSONArray ja = _executeQuery(driverClassName, jdbcConnectionUrl, jndiName, user, pass, queryToExecute, useDataSource);
	out.println(" - query result is:\n");
	out.println(ja.toString());
} catch (Exception ex) {
	out.println("error: " + e.getMessage());
	e.printStackTrace(out);
}


JSONArray _executeQuery(driverClassName, jdbcConnectionUrl, jndiName, user, pass, queryToExecute, useDataSource) {
	ResultSet rs = null;
	Connection conn = null;
	Driver d = null;
	DataSource ds = null;
	JSONArray result = null;
	try {
		Properties props = new Properties();
		out.println(" - connecting to database: " + jdbcConnectionUrl + " using " + (useDataSource ? ("jndi name:" + jndiName) : ("driver:" + driverClassName)));
		if (useDataSource) {
			ds = DataSourceFactoryUtil.initDataSource(driverClassName, jdbcConnectionUrl, user, pass, jndiName)
			conn = ds.getConnection();
		} else {
			d = (Driver) Class.forName(driverClassName ).newInstance();
			props.put("user",user);
			props.put("password",pass);
			conn = d.connect(jdbcConnectionUrl, props);
		}
		Statement stmt = conn.createStatement();
		out.println(" - executing query: " + queryToExecute + "");
		stmt.execute(queryToExecute);
		rs = stmt.getResultSet();
		result = _toJSONArray(rs);
	} catch (Exception e) {
		throw e;
	} finally {
		if (rs != null) {rs.close(); }
		if (conn != null) {conn.close()};
	}
	return result;
}
	JSONArray _toJSONArray(ResultSet rs) throws Exception {
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			int numColumns = rsmd.getColumnCount();
			JSONObject obj = JSONFactoryUtil.createJSONObject();
			for (int i = 1; i < numColumns + 1; i++) {
				try {
					String column_name = rsmd.getColumnName(i);
					if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
						obj.put(column_name, rs.getArray(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
						obj.put(column_name, rs.getInt(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
						obj.put(column_name, rs.getBoolean(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
						obj.put(column_name, rs.getBlob(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
						obj.put(column_name, rs.getDouble(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
						obj.put(column_name, rs.getFloat(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
						obj.put(column_name, rs.getInt(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
						obj.put(column_name, rs.getNString(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
						obj.put(column_name, rs.getString(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
						obj.put(column_name, rs.getInt(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
						obj.put(column_name, rs.getInt(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
						obj.put(column_name, rs.getDate(column_name));
					} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
						obj.put(column_name, rs.getTimestamp(column_name));
					} else {
						obj.put(column_name, rs.getObject(column_name));
					}

				} catch (Exception ex) {

				}
			}

			ja.put(obj);
		}
		return ja;
	}