import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementierung des BillBoard-Servers.
 * In dieser Version unterstützt er asynchrone Aufrufe.
 * Damit wird die Implementierung von Long Polling möglich:
 * Anfragen (HTTP GET) werden nicht sofort wie bei zyklischem
 * Polling beantwortet sondern verbleiben so lange im System,
 * bis eine Änderung an den Client gemeldet werden kann.
 *
 * @author heikerli
 */
@WebServlet(asyncSupported = true, urlPatterns = {"/BillBoardServer"})
public class BillBoardServlet extends HttpServlet {
	//private final BillBoardHtmlAdapter bb = new BillBoardHtmlAdapter ("BillBoardServlet");
	private final BillBoardJSONAdapter bb = new BillBoardJSONAdapter("BillBoardServlet");
	private HashMap<String, AsyncContext> events = new HashMap<>();
	private static int eventId = 0;
	
	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String caller_ip = request.getRemoteAddr();
		/* Ausgabe des gesamten Boards */
		System.out.println("BillBoardServer - GET (" + caller_ip + "): full output");
		//response.setContentType("text/html;charset=UTF-8");
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		
		//to clear threads and allow for asynchronous execution
		final AsyncContext asyncContext = request.startAsync(request, response);
		asyncContext.setTimeout(0);
		
		//add context to list for later use
		if(!events.containsKey(caller_ip)){
			events.put(caller_ip, asyncContext);
			sendUpdates();
		}
		
		System.out.println("Registered async event source");
	}
	
	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String caller_ip = request.getRemoteAddr();
		System.out.println ("BillBoardServer - POST (" + caller_ip + ")");
		
		try{
			bb.createEntry(request.getParameter("message"), caller_ip);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// TODO implementation of doPost()!
		response.getWriter().close();
		sendUpdates();
	}
	
	/**
	 * Handles the HTTP <code>DELETE</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String caller_ip = request.getRemoteAddr();
		System.out.println ("BillBoardServer - DELETE (" + caller_ip + ")");
		try{
			bb.deleteEntry(Integer.parseInt(request.getParameter("idx")));
		} catch(Exception e){
			e.printStackTrace();
		}
		// TODO implementation of doDelete()!
		response.getWriter().close();
		sendUpdates();
	}
	
	/**
	 * Handles the HTTP <code>PUT</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String caller_ip = request.getRemoteAddr();
		System.out.println ("BillBoardServer - PUT (" + caller_ip + ")");
		try {
			bb.updateEntry(Integer.parseInt(request.getParameter("idx")), request.getParameter("message"), caller_ip);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// TODO implementation of doPut()!
		response.getWriter().close();
		sendUpdates();
	}
	
	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "BillBoard Servlet";
	}// </editor-fold>
	
	private void sendUpdates(){
		System.out.println("Sending updates");
		for(AsyncContext context: events.values()){
			PrintWriter writer;
			try {
				writer = context.getResponse().getWriter();
				String table = bb.readEntries(context.getRequest().getRemoteAddr());
				System.out.println("Content: "+table);
				
				writer.print("id: ");
				writer.println(eventId++);
				writer.print("data: ");
				writer.println(table);
				writer.println();
				writer.println(table);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
