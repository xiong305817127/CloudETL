package test.sql;

import java.sql.SQLException;
import java.util.List;

import org.apache.phoenix.parse.AliasedNode;
import org.apache.phoenix.parse.BindableStatement;
import org.apache.phoenix.parse.SQLParser;
import org.apache.phoenix.parse.SelectStatement;

public class PhoenixSQLTest {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		String sql1 = "select * from tb_name1 t1 ,tb_name2 t2 where id=12 ";
		//String sql2 = "select * from tb_name1 where id=12  order by id desc limit 100";
		SQLParser paser = new SQLParser(sql1);
		BindableStatement statement =	paser.parseStatement();

		if(statement instanceof SelectStatement){
			SelectStatement real = (SelectStatement) statement;
			System.out.println(real.getFrom());
			System.out.println(real.getGroupBy());
			System.out.println(real.getHint());
			System.out.println(real.getLimit());
			
			List<AliasedNode> nods = real.getSelect();
			for (AliasedNode aliasedNode : nods) {
				System.out.println(aliasedNode.getNode());
			}
		}
	}

}
