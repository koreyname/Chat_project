/*
//create by tyz@cuit
//create date is 2023.6.2
*/
package run;
import java.sql.*;
public class Check_info {
    private String name;
    private String pwd;

    public void setName(String name) {
        this.name = name;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }
    private static Connection con;
    private static Statement stmt;

    public String getPwd() {
        return pwd;
    }
    public void Close()
    {
        try {
            con.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }
    public Boolean SQL_Query(Check_info c)
    {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project3", "tim", "123456");
            stmt=con.createStatement();
            String sql="select * from info";
            ResultSet rs=this.stmt.executeQuery(sql);
            while(rs.next()) {
                if(this.getName().equals(rs.getString("name"))&&this.getPwd().equals(rs.getString("pwd"))) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int Register(String sql)
    {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project3", "tim", "123456");
            stmt=con.createStatement();
            return this.stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Boolean add(Check_info c)
    {
        String sql="insert into info(name,pwd) value('"+c.getName()+"','"+c.getPwd()+"')";
        if(Register(sql)>0)
        {
            return true;
        }
        return false;
    }

}
