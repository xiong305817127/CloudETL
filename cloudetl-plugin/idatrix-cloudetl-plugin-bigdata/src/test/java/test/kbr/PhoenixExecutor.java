/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package test.kbr;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PhoenixExecutor implements Runnable {

	private Connection conn;

	private String query;

	private long wait;

	public PhoenixExecutor(Connection conn, String query, long wait) {
		super();
		this.conn = conn;
		this.query = query;
		this.wait = wait;
	}

	@Override
	public void run() {
			try {
				printPath();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void printPath() throws SQLException {
		/*QueryRunner queryRunner = new QueryRunner();
		try {
			List<Map<String, Object>> list = queryRunner.query(conn, query, new MapListHandler());
			System.out.println( Arrays.toString(list.toArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtils.closeQuietly(conn);
		}*/
		
		Statement state = conn.createStatement();
		System.out.println("DEBUG >>> query: " + query);
		System.out.println("DEBUG >>> result:" + state.execute(query));
		System.out.println("DEBUG >>> getUpdateCount: " + state.getUpdateCount());
		System.out.println("DEBUG >>> getResultSet -> getRow: " + (state.getResultSet() != null ? state.getResultSet().getRow() : "NULL"));
	}
}
