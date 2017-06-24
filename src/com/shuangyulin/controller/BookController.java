package com.shuangyulin.controller;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.shuangyulin.dao.BookDAO;
import com.shuangyulin.dao.BookTypeDAO;
import com.shuangyulin.domain.Book; 
import com.shuangyulin.domain.BookType;
import com.shuangyulin.utils.ExportExcelUtil;
import com.shuangyulin.utils.UserException;


//图书管理控制层
@Controller
@RequestMapping("/Book")
public class BookController {

	//注入业务层对象
	@Resource BookDAO bookDAO;
	@Resource BookTypeDAO bookTypeDAO;
	
	
	@InitBinder("bookType")
	public void initBinderBookType(WebDataBinder binder) {
		binder.setFieldDefaultPrefix("bookType.");
	}
	
	@InitBinder
	// 必须有一个参数WebDataBinder
	public void initBinder(WebDataBinder binder) {
		//System.out.println(binder.getFieldDefaultPrefix());
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("yyyy-MM-dd"), false));
	 
		binder.registerCustomEditor(Integer.class, new PropertyEditorSupport() {
			@Override
			public String getAsText() { 
				return (getValue() == null) ? "" : getValue().toString();
			} 
			@Override
			public void setAsText(String text) {
				Integer value = null;
				if (null != text && !text.equals("")) {  
						try {
							value = Integer.valueOf(text);
						} catch(Exception ex)  { 
							throw new UserException("数据格式输入不正确！"); 
						}  
				}
				setValue(value);
			} 
		});
	 
		
		//binder.registerCustomEditor(Integer.class, null,new CustomNumberEditor(Integer.class, null, true));
		
		binder.registerCustomEditor(Float.class, new PropertyEditorSupport() {
			@Override
			public String getAsText() { 
				return (getValue() == null)? "" : getValue().toString();
			} 
			@Override
			public void setAsText(String text)  {
				Float value = null;
				if (null != text && !text.equals("")) {
					try {
						value = Float.valueOf(text);
					} catch (Exception e) { 
						throw new UserException("数据格式输入不正确！"); 
					}
				}
				setValue(value);
			}
		});
	 
	}
	
	
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model,HttpServletRequest request) {
		model.addAttribute(new Book());
		/*查询所有的BookType信息*/
        List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
        request.setAttribute("bookTypeList", bookTypeList);
		return "Book_add";
	}
	
	

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Validated Book book, BindingResult br,
			Model model, HttpServletRequest request) throws UnsupportedEncodingException {
		 
		if (br.hasErrors()) {
			model.addAttribute(book);
			/*查询所有的BookType信息*/
	        List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
	        request.setAttribute("bookTypeList", bookTypeList);
			return "Book_add";
		}
		
		if(bookDAO.GetBookByBarcode(book.getBarcode()) != null) {
			throw new UserException("图书条形码已经存在！"); 
		}
		
		   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        
        /**构建图片保存的目录**/    
        String photoBookPathDir = "/upload";     
        /**得到图片保存目录的真实路径**/    
        String photoBookRealPathDir = request.getSession().getServletContext().getRealPath(photoBookPathDir);     
        /**根据真实路径创建目录**/    
        File photoBookSaveFile = new File(photoBookRealPathDir);     
        if(!photoBookSaveFile.exists())     
        	photoBookSaveFile.mkdirs();           
        /**页面控件的文件流**/    
        MultipartFile multipartFile_photoBook = multipartRequest.getFile("photoBookFile");   
        
        book.setPhotoBook("upload/NoImage.jpg");
        if(!multipartFile_photoBook.isEmpty()) {
        	/**获取文件的后缀**/    
            String suffix = multipartFile_photoBook.getOriginalFilename().substring  
                            (multipartFile_photoBook.getOriginalFilename().lastIndexOf("."));     
            /**使用UUID生成文件名称**/    
            String photoBookFileName = UUID.randomUUID().toString()+ suffix;//构建文件名称     
            //String logImageName = multipartFile.getOriginalFilename();  
            /**拼成完整的文件保存路径加文件**/    
            String photoBookFilePath = photoBookRealPathDir + File.separator  + photoBookFileName;                
            File photoBookFile = new File(photoBookFilePath);          
            
            try {     
                multipartFile_photoBook.transferTo(photoBookFile);     
            } catch (IllegalStateException e) {     
                e.printStackTrace();     
            } catch (IOException e) {            
                e.printStackTrace();     
            }
            book.setPhotoBook("upload/" + photoBookFileName);
        }
         
        
		
		try {
			bookDAO.AddBook(book);
			request.setAttribute("message", java.net.URLEncoder.encode(
					"图书添加成功!", "GBK"));
			return "message";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", java.net.URLEncoder
					.encode("图书添加失败!","GBK"));
			return "error";
		}

	}
	
	
	
	
	@RequestMapping(value = { "/list" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String list(String barcode,@ModelAttribute BookType bookType,String bookName,String publishDate,Integer currentPage, Model model, HttpServletRequest request) {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (bookName == null) bookName = "";
		if (barcode == null) barcode = "";
		if (publishDate == null) publishDate = "";
		List<Book> bookList = bookDAO.QueryBookInfo(bookName, bookType, barcode,publishDate, currentPage);
	    /*计算总的页数和总的记录数*/
	    bookDAO.CalculateTotalPageAndRecordNumber(bookName, bookType, barcode,publishDate);
	    /*获取到总的页码数目*/
	    int totalPage = bookDAO.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = bookDAO.getRecordNumber();
	    request.setAttribute("bookList",  bookList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("bookName", bookName);
	    request.setAttribute("bookType", bookType);
	    List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
	    request.setAttribute("bookTypeList", bookTypeList);
	    request.setAttribute("barcode", barcode);
	    request.setAttribute("publishDate", publishDate);
	     
		return "Book_query_result"; 
	}
	

	@RequestMapping(value = { "/frontlist" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String frontlist(String barcode,@ModelAttribute BookType bookType,String bookName,String publishDate,Integer currentPage, Model model, HttpServletRequest request) {
		if (currentPage==null || currentPage == 0) currentPage = 1;
		if (bookName == null) bookName = "";
		if (barcode == null) barcode = "";
		if (publishDate == null) publishDate = "";
		List<Book> bookList = bookDAO.QueryBookInfo(bookName, bookType, barcode,publishDate, currentPage);
	    /*计算总的页数和总的记录数*/
	    bookDAO.CalculateTotalPageAndRecordNumber(bookName, bookType, barcode,publishDate);
	    /*获取到总的页码数目*/
	    int totalPage = bookDAO.getTotalPage();
	    /*当前查询条件下总记录数*/
	    int recordNumber = bookDAO.getRecordNumber();
	    request.setAttribute("bookList",  bookList);
	    request.setAttribute("totalPage", totalPage);
	    request.setAttribute("recordNumber", recordNumber);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("bookName", bookName);
	    request.setAttribute("bookType", bookType);
	    List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
	    request.setAttribute("bookTypeList", bookTypeList);
	    request.setAttribute("barcode", barcode);
	    request.setAttribute("publishDate", publishDate);
	     
		return "Book_frontquery_result"; 
	}
	
	
	
	
	@RequestMapping(value="/{barcode}/update",method=RequestMethod.GET)
	public String update(@PathVariable String barcode,Model model,HttpServletRequest request) {
		/*根据主键barcode获取Book对象*/
        Book book = bookDAO.GetBookByBarcode(barcode);

        List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
        request.setAttribute("bookTypeList", bookTypeList);
        request.setAttribute("book",  book);
        return "Book_modify";
		 
	}
	
	@RequestMapping(value="/{barcode}/frontshow",method=RequestMethod.GET)
	public String frontshow(@PathVariable String barcode,Model model,HttpServletRequest request) {
		/*根据主键barcode获取Book对象*/
        Book book = bookDAO.GetBookByBarcode(barcode);

        List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
        request.setAttribute("bookTypeList", bookTypeList);
        request.setAttribute("book",  book);
        return "Book_frontshow";
		 
	}
	
	

	@RequestMapping(value = "/{barcode}/update", method = RequestMethod.POST)
	public String update(@Validated Book book, BindingResult br,
			Model model, HttpServletRequest request) throws UnsupportedEncodingException {
		if (br.hasErrors()) { 
			model.addAttribute(book);
			/*查询所有的BookType信息*/
	        List<BookType> bookTypeList = bookTypeDAO.QueryAllBookTypeInfo();
	        request.setAttribute("bookTypeList", bookTypeList);
			return "Book_modify";
		}
		
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        
        /**构建图片保存的目录**/    
        String photoBookPathDir = "/upload";     
        /**得到图片保存目录的真实路径**/    
        String photoBookRealPathDir = request.getSession().getServletContext().getRealPath(photoBookPathDir);     
        /**根据真实路径创建目录**/    
        File photoBookSaveFile = new File(photoBookRealPathDir);     
        if(!photoBookSaveFile.exists())     
        	photoBookSaveFile.mkdirs();           
        /**页面控件的文件流**/    
        MultipartFile multipartFile_photoBook = multipartRequest.getFile("photoBookFile");   
      
        if(!multipartFile_photoBook.isEmpty()) {
        	/**获取文件的后缀**/    
            String suffix = multipartFile_photoBook.getOriginalFilename().substring  
                            (multipartFile_photoBook.getOriginalFilename().lastIndexOf("."));     
            /**使用UUID生成文件名称**/    
            String photoBookFileName = UUID.randomUUID().toString()+ suffix;//构建文件名称     
            //String logImageName = multipartFile.getOriginalFilename();  
            /**拼成完整的文件保存路径加文件**/    
            String photoBookFilePath = photoBookRealPathDir + File.separator  + photoBookFileName;                
            File photoBookFile = new File(photoBookFilePath);          
            
            try {     
                multipartFile_photoBook.transferTo(photoBookFile);     
            } catch (IllegalStateException e) {     
                e.printStackTrace();     
            } catch (IOException e) {            
                e.printStackTrace();     
            }
            book.setPhotoBook("upload/" + photoBookFileName);
        }
         
        
        
		try {
			BookType bookType = bookTypeDAO.GetBookTypeByBookTypeId(book.getBookType().getBookTypeId());
			book.setBookType(bookType);
			bookDAO.UpdateBook(book);
			request.setAttribute("message", java.net.URLEncoder.encode(
					"图书更新成功!", "GBK"));
			return "message";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", java.net.URLEncoder.encode("图书更新失败!","GBK"));
			return "error";
		} 
	}
	
	
	@RequestMapping(value="/{barcode}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable String barcode,HttpServletRequest request) throws UnsupportedEncodingException {
		  try { 
			  bookDAO.DeleteBook(barcode);
	           
	            request.setAttribute("message", java.net.URLEncoder.encode(
						"图书删除成功!", "GBK"));
	            return "message";
	        } catch (Exception e) { 
	            e.printStackTrace();
	            request.setAttribute("error", java.net.URLEncoder
						.encode("图书删除失败!","GBK"));
				return "error";
	        }
	}
	
	
	

	@RequestMapping(value = { "/OutToExcel" }, method = {RequestMethod.GET,RequestMethod.POST})
	public String OutToExcel(String barcode,@ModelAttribute BookType bookType,String bookName,String publishDate, Model model, HttpServletRequest request,HttpServletResponse response) {
		 if(bookName == null) bookName = "";
	     if(publishDate == null) publishDate = "";
	     List<Book> bookList = bookDAO.QueryBookInfo(bookName,bookType,barcode,publishDate);
	        ExportExcelUtil ex = new ExportExcelUtil();
	        String title = "Book信息记录"; 
	        String[] headers = { "图书条形码","图书名称","图书所在类别","图书价格","库存","出版社","出版日期","图书图片"};
	        List<String[]> dataset = new ArrayList<String[]>(); 
	        for(int i=0;i<bookList.size();i++) {
	        	Book book = bookList.get(i); 
	        	dataset.add(new String[]{book.getBarcode(),book.getBookName(),book.getBookType().getBookTypeName(),
	book.getPrice() + "",book.getCount() + "",book.getPublish(),book.getPublishDate(),book.getPhotoBook()});
	        }
	        /*
	        OutputStream out = null;
			try {
				out = new FileOutputStream("C://output.xls");
				ex.exportExcel(title,headers, dataset, out);
			    out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			 
			OutputStream out = null;//创建一个输出流对象 
			try {  
				out = response.getOutputStream();//
				response.setHeader("Content-disposition","attachment; filename="+"Book.xls");//filename是下载的xls的名，建议最好用英文 
				response.setContentType("application/msexcel;charset=UTF-8");//设置类型 
				response.setHeader("Pragma","No-cache");//设置头 
				response.setHeader("Cache-Control","no-cache");//设置头 
				response.setDateHeader("Expires", 0);//设置日期头  
				String rootPath = request.getSession().getServletContext().getRealPath("/");
				ex.exportExcel(rootPath,title,headers, dataset, out);
				out.flush();
			} catch (IOException e) { 
				e.printStackTrace(); 
			}finally{
				try{
					if(out!=null){ 
						out.close(); 
					}
				}catch(IOException e){ 
					e.printStackTrace(); 
				} 
			}
			return null;
	}
	
	
	
	
	
	
	
	
	
}
