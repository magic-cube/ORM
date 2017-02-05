package sorm.po;

import java.sql.*;
import java.util.*;

public class Hc {

	private Integer id;
	private String uname;
	public Integer getId(){
		return id ;
	}
	public String getUname(){
		return uname ;
	}
	public void setId(Integer id){
		this.id=id;
	}
	public void setUname(String uname){
		this.uname=uname;
	}
}
