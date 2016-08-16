package com.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.mapred.JobConf;

import com.model.HdfsDAO;
 

/**
 * Servlet implementation class UploadServlet
 */
@SuppressWarnings({ "unused", "deprecation" })
public class UploadServlet extends HttpServlet {
 
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   request.setCharacterEncoding("UTF-8");
		   File file ;
		   int maxFileSize = 50 * 1024 *1024;  //50M
		   int maxMemSize = 50 * 1024 *1024;    //50M
		   ServletContext context = getServletContext();
		   String filePath = context.getInitParameter("file-upload");
			System.out.println("source file path:"+filePath+"");
		   // ��֤�ϴ�����������
		   String contentType = request.getContentType();
		   if ((contentType.indexOf("multipart/form-data") >= 0)) {

		      DiskFileItemFactory factory = new DiskFileItemFactory();
		      // �����ڴ��д洢�ļ������ֵ
		      factory.setSizeThreshold(maxMemSize);
		      // ���ش洢�����ݴ��� maxMemSize.
		      factory.setRepository(new File("c:\\temp"));

		      // ����һ���µ��ļ��ϴ��������
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      // ��������ϴ����ļ���С
		      upload.setSizeMax( maxFileSize );
		      try{ 
		         // ������ȡ���ļ�
		         List fileItems = upload.parseRequest(request);

		         // �����ϴ����ļ�
		         Iterator i = fileItems.iterator();

		         System.out.println("begin to upload file to tomcat server</p>"); 
		         while ( i.hasNext () ) 
		         {
		            FileItem fi = (FileItem)i.next();
		            if ( !fi.isFormField () )	
		            {
		            // ��ȡ�ϴ��ļ��Ĳ���
		            String fieldName = fi.getFieldName();
		            String fileName = fi.getName();
		            
		            String fn = fileName.substring( fileName.lastIndexOf("\\")+1);
		            System.out.println("<br>"+fn+"<br>");
		            boolean isInMemory = fi.isInMemory();
		            long sizeInBytes = fi.getSize();
		            // д���ļ�
		            if( fileName.lastIndexOf("\\") >= 0 ){
		            file = new File( filePath , 
		            fileName.substring( fileName.lastIndexOf("\\"))) ;
		            //out.println("filename"+fileName.substring( fileName.lastIndexOf("\\"))+"||||||");
		            }else{
		            file = new File( filePath ,
		            fileName.substring(fileName.lastIndexOf("\\")+1)) ;
		            }
		            fi.write( file ) ;
		            System.out.println("upload file to tomcat server success!");
		            
		            System.out.println("begin to upload file to hadoop hdfs</p>"); 
		            //��tomcat�ϵ��ļ��ϴ���hadoop��
		            String username = (String) request.getSession().getAttribute("username");
		            JobConf conf = HdfsDAO.config();
		            HdfsDAO hdfs = new HdfsDAO(conf);
		            hdfs.copyFile(filePath+"\\"+fn, "/"+username+"/"+fn);
		            System.out.println("upload file to hadoop hdfs success!");
		           
		            System.out.println("username-----"+username);
 		    		FileStatus[] list = hdfs.ls("/"+username);
 		    		 request.setAttribute("list",list);
		            request.getRequestDispatcher("index.jsp").forward(request, response);
		            //request.getRequestDispatcher("DocumentServlet.java").forward(request, response);
		            }
		         }
		      }catch(Exception ex) {
		         System.out.println(ex);
		      }
		   }else{
		      System.out.println("<p>No file uploaded</p>"); 
		
		   }
	 
	      
		 
	}

}
