package cn.ljj.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ljj.server.ServerThread;
import cn.ljj.server.log.Log;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		Log.e("MainServlet", "--------------"
				+ "Starting Server---------------");
		ServerThread mServer = new ServerThread();
		new Thread(mServer).start();
		Log.e("MainServlet", "StartupInit inited");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	
	
}
