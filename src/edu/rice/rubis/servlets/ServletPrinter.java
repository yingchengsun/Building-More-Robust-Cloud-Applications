package edu.rice.rubis.servlets;

import java.util.List;
import java.util.Iterator;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletResponse;

import net.sf.hibernate.Session;
import net.sf.hibernate.Query;

import edu.rice.rubis.hibernate.Category;
import edu.rice.rubis.hibernate.User;
import edu.rice.rubis.hibernate.Region;
import edu.rice.rubis.hibernate.Item;
import edu.rice.rubis.hibernate.Bid;
import edu.rice.rubis.hibernate.Comment;

/** In fact, this class is not a servlet itself but it provides
 * output services to servlets to send back HTML files.
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

/**
 * This class is modified on the next parts: Two methods are added:
 * printRecommedHeader() printRecommed() One function is added in the
 * printItemDescription() function: print item's pictures
 * 
 * @author <a href="yxs489@case.edu">Yingcheng Sun</a>
 * @version 1.1
 */

public class ServletPrinter {
	private PrintWriter out;
	private String servletName;
	private GregorianCalendar startDate;

	public ServletPrinter(HttpServletResponse toWebServer,
			String callingServletName) {
		startDate = new GregorianCalendar();
		toWebServer.setContentType("text/html");
		try {
			out = toWebServer.getWriter();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		servletName = callingServletName;
	}

	// void printPictures()
	// {
	// try
	// {
	// out.println(
	// "<IMG SRC=\"/rubis/images/IMG_1013.JPG.jpg\" height=22 width=90></a>");
	// }
	// catch (Exception e)
	// {
	// out.println("Unable to print pictures)<br>");
	// }
	// }
	void printFile(String filename) {
		FileReader fis = null;
		try {
			fis = new FileReader(filename);
			char[] data = new char[4 * 1024]; // 4K buffer
			int bytesRead;
			bytesRead = fis.read(data);
			while (/* (bytesRead = fis.read(data)) */
			bytesRead != -1) {
				out.write(data, 0, bytesRead);
				bytesRead = fis.read(data);
			}
		} catch (Exception e) {
			out.println("Unable to read file (exception: " + e + ")<br>");
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (Exception ex) {
					out.println("Unable to close the file reader (exception: "
							+ ex + ")<br>");
				}
		}
	}

	void printHTMLheader(String title) {
		printFile(Config.HTMLFilesPath + "/header.html");
		out.println("<title>" + title + "</title>");
	}

	void printHTMLheader_for_about_me(String title) {
		 printFile(Config.HTMLFilesPath + "/header_for_about_me.jsp");
		 out.println("<title>" + title + "</title>");
		// out.println("<!DOCTYPE html><html>");
		// out.println("<title>" + title + "</title>");
		// out.println("<head><link href=\"css.css\" rel=\"stylesheet\"  type=\"text/css\"/></head>");

		// out.println("<head><link rel='Stylesheet' href='css.css' type='text/css'></head>");
		// out.println("<body>");
		// out.println("<a href=\"ddd\"/>");
		//out.print("<!DOCTYPE html><html><head><link rel='Stylesheet' type='text/css' href='/rubis/css.css'/></head><body>");
	}

	void printHTMLfooter() {
		GregorianCalendar endDate = new GregorianCalendar();

		out.println("<br><hr>RUBiS (C) Rice University/INRIA<br><i>Page generated by "
				+ servletName
				+ " in "
				+ TimeManagement.diffTime(startDate, endDate) + "</i><br>");
		out.println("</body>");
		out.println("</html>");
	}

	void printHTML(String msg) {
		out.println(msg);
	}

	void printHTMLHighlighted(String msg) {
		//out.println("<TABLE width=\"100%\" bgcolor=\"#CCCCFF\">");
		//out.println("<TR><TD align=\"center\" width=\"100%\"><FONT size=\"4\" color=\"#000000\"><B>"
		out.println("<TABLE id=\"table\" >");
		
		SessionCount sc = new SessionCount();
//		if (sc.get_a() < sc.get_peakAfterCut()) {
//			out.println("<TR><Th align=\"center\" ><FONT color=\"#FFFFFF\"><B>"
//					+ msg + "</B></FONT></Th></TR>");
//		}else
//		{
			out.println("<TR><Th align=\"center\" ><FONT color=\"#000000\"><B>"
					+ msg + "</B></FONT></Th></TR>");
		//}
		
		out.println("</TABLE><p>");
	}
	
	// //////////////////////////////////////
	// Category related printed functions //
	// //////////////////////////////////////

	void printCategory(Category category) {
		try {
			out.println("<a href=\"/rubis/servlet/SearchItemsByCategory?category="
					+ category.getId()
					+ "&categoryName="
					+ URLEncoder.encode(category.getName())
					+ "\">"
					+ category.getName() + "</a><br>");
		} catch (Exception e) {
			out.println("Unable to print Category (exception: " + e + ")<br>");
		}
	}

	/** List all the categories with links to browse items by region */
	void printCategoryByRegion(Category category, Region region) {
		try {
			out.println("<a href=\"/rubis/servlet/SearchItemsByRegion?category="
					+ category.getId()
					+ "&categoryName="
					+ URLEncoder.encode(category.getName())
					+ "&region="
					+ region.getId() + "\">" + category.getName() + "</a><br>");
		} catch (Exception e) {
			out.println("Unable to print Category (exception: " + e + ")<br>");
		}
	}

	/** Lists all the categories and links to the sell item page */
	void printCategoryToSellItem(Category category, User user) {
		try {
			out.println("<a href=\"/rubis/servlet/SellItemForm?category="
					+ category.getId() + "&user=" + user.getId() + "\">"
					+ category.getName() + "</a><br>");
		} catch (Exception e) {
			out.println("Unable to print Category (exception: " + e + ")<br>");
		}
	}

	// ////////////////////////////////////
	// Region related printed functions //
	// ////////////////////////////////////

	void printRegion(Region region) {
		try {
			out.println("<a href=\"/rubis/servlet/BrowseCategories?region="
					+ URLEncoder.encode(region.getName()) + "\">"
					+ region.getName() + "</a><br>");
		} catch (Exception e) {
			out.println("Unable to print Region (exception: " + e + ")<br>");
		}
	}

	// ////////////////////////////////////
	// Item related printed functions //
	// ////////////////////////////////////

	void printItemHeader() {
		out.println("<TABLE border=\"1\" summary=\"List of items\">"
				+ "<THEAD>"
				+ "<TR><TH>Designation<TH>Price<TH>Bids<TH>End Date<TH>Bid Now"
				+ "<TBODY>");
	}

	void printItem(Item item) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId()
					+ "\">"
					+ item.getName()
					+ "<TD>"
					+ item.getMaxBid()
					+ "<TD>"
					+ item.getNbOfBids()
					+ "<TD>"
					+ item.getEndDate()
					+ "<TD><a href=\"/rubis/servlet/PutBidAuth?itemId="
					+ item.getId()
					+ "\"><IMG SRC=\"/rubis/images/bid_now.jpg\" height=22 width=90></a>");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>");
		}
	}

	void printItemFooter() {
		out.println("</TABLE>");
	}

	void printItemFooter(String URLprevious, String URLafter) {
		out.println("</TABLE>\n");
		out.println("<p><CENTER>\n" + URLprevious + "\n&nbsp&nbsp&nbsp"
				+ URLafter + "\n</CENTER>\n");
	}

	/**
	 * Print the full description of an item and the bidding option if user !=
	 * null
	 */
	void printItemDescription(Item item, Float maxBid, Integer nbOfBids,
			User user, Session sess) {
		// printHTML("1 " + item +
		// "2  "+maxBid+"3  "+nbOfBids+"4  "+user+"5  "+sess);
		if (item.getQuantity().intValue() > 1) {
			try {
				/*
				 * Get the qty max first bids and parse bids in this order until
				 * qty is reached. The bid that reaches qty is the current
				 * minimum bid.
				 */
				Query q = sess.createFilter(item.getBids(),
						"select this order by this.bid desc");
				q.setMaxResults(item.getQuantity().intValue());
				List lst = q.list();
				Iterator it = lst.iterator();

				if (it.hasNext()) {
					int numberOfItems = 0;
					do {
						Bid bid = (Bid) it.next();
						numberOfItems = numberOfItems + bid.getQty().intValue();
						if (numberOfItems >= item.getQuantity().intValue()) {
							maxBid = bid.getMaxBid();
							break;
						}
					} while (it.hasNext());
				}
			} catch (Exception e) {
				printHTML("Problem while computing current bid: " + e + "<br>");
				return;
			}
		}

		if (user != null) {
			printHTMLheader("RUBiS: Bidding\n");
			printHTMLHighlighted("You are ready to bid on: " + item.getName());
		} else {
			printHTMLheader("RUBiS: Viewing " + item.getName() + "\n");
			printHTMLHighlighted(item.getName());
		}
		out.println("<TABLE>\n" + "<TR><TD>Currently<TD><b><BIG>" + maxBid
				+ "</BIG></b>\n");
		// Check if the reservePrice has been met (if any)
		if (item.getReservePrice().floatValue() > 0) { // Has the reserve price
														// been met ?
			if (maxBid.floatValue() >= item.getReservePrice().floatValue())
				out.println("(The reserve price has been met)\n");
			else
				out.println("(The reserve price has NOT been met)\n");
		}
		out.println("<TR><TD>Quantity<TD><b><BIG>"
				+ item.getQuantity()
				+ "</BIG></b>\n"
				+ "<TR><TD>First bid<TD><b><BIG>"
				+ maxBid
				+ "</BIG></b>\n"
				+ "<TR><TD># of bids<TD><b><BIG>"
				+ nbOfBids
				+ "</BIG></b> (<a href=\"/rubis/servlet/ViewBidHistory?itemId="
				+ item.getId()
				+ "\">bid history</a>)\n"
				+ "<TR><TD>Seller<TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
				+ item.getSeller().getId() + "\">"
				+ item.getSeller().getNickname()
				+ "</a> (<a href=\"/rubis/servlet/PutCommentAuth?to="
				+ item.getSeller().getId() + "&itemId=" + item.getId()
				+ "\">Leave a comment on this user</a>)\n"
				+ "<TR><TD>Started<TD>" + item.getStartDate() + "\n"
				+ "<TR><TD>Ends<TD>" + item.getEndDate() + "\n" + "</TABLE>");
		// Can the user buy this item now ?
		if (item.getBuyNow().floatValue() > 0)
			out.println("<p><a href=\"/rubis/servlet/BuyNowAuth?itemId="
					+ item.getId()
					+ "\">"
					+ "<IMG SRC=\"/rubis/images/buy_it_now.jpg\" height=22 width=150></a>"
					+ "  <BIG><b>You can buy this item right now for only $"
					+ item.getBuyNow() + "</b></BIG><br><p>\n");

		if (user == null) {
			out.println("<a href=\"/rubis/servlet/PutBidAuth?itemId="
					+ item.getId()
					+ "\"><IMG SRC=\"/rubis/images/bid_now.jpg\" height=22 width=90> on this item</a>\n");
		}

		//printHTMLHighlighted("Item description");
		//out.println("<TR align=\"center\"><TD>Item description<TD>\n");
		out.println("<TABLE align=\"center\">\n" + "<TR><TD><BIG>Item description</BIG><TD>\n"+ "</TABLE>");
		
		// print item's pictures
		SessionCount sc = new SessionCount();
		if (sc.get_a() >= sc.get_peak()) {
			out.println(item.getDescription());
		}else{
			out.println("<IMG SRC=\"/rubis/images/" + item.getPictures()
					+ "\" ></a>\n");
			out.println("<br><p>\n");
		}
		
		out.println("<br><p>\n");

		if (user != null) {
			printHTMLHighlighted("Bidding");
			Float minBid = new Float(maxBid.floatValue() + 1);
			printHTML("<form action=\"/rubis/servlet/StoreBid\" method=POST>\n"
					+ "<input type=hidden name=minBid value=" + minBid + ">\n"
					+ "<input type=hidden name=userId value=" + user.getId()
					+ ">\n" + "<input type=hidden name=itemId value="
					+ item.getId() + ">\n"
					+ "<input type=hidden name=maxQty value="
					+ item.getQuantity() + ">\n" + "<center><table>\n"
					+ "<tr><td>Your bid (minimum bid is " + minBid
					+ "):</td>\n"
					+ "<td><input type=text size=10 name=bid></td></tr>\n"
					+ "<tr><td>Your maximum bid:</td>\n"
					+ "<td><input type=text size=10 name=maxBid></td></tr>\n");
			if (item.getQuantity().intValue() > 1)
				printHTML("<tr><td>Quantity:</td>\n"
						+ "<td><input type=text size=5 name=qty></td></tr>\n");
			else
				printHTML("<input type=hidden name=qty value=1>\n");

			printHTML("</table><p><input type=submit value=\"Bid now!\"></center><p>\n");
		}

	}

	// //////////////////////////////////////
	// About me related printed functions //
	// //////////////////////////////////////

	void printUserBidsHeader() {
		printHTMLHighlighted("<p><h3>Items you have bid on.</h3>\n");
		out.println("<TABLE border=\"1\" summary=\"Items You've bid on\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Your max bid<TH>Quantity"
				+ "<TH>Start Date<TH>End Date<TH>Seller<TH>Put a new bid\n"
				+ "<TBODY>\n");
	}

	void printItemUserHasBidOn(Item item, Float maxBid, User user) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId()
					+ "\">"
					+ item.getName()
					+ "<TD>"
					+ item.getInitialPrice()
					+ "<TD>"
					+ item.getMaxBid()
					+ "<TD>"
					+ maxBid
					+ "<TD>"
					+ item.getQuantity()
					+ "<TD>"
					+ item.getStartDate()
					+ "<TD>"
					+ item.getEndDate()
					+ "<TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ item.getSeller().getId()
					+ "\">"
					+ item.getSeller().getNickname()
					+ "<TD><a href=\"/rubis/servlet/PutBid?itemId="
					+ item.getId()
					+ "&nickname="
					+ user.getNickname()
					+ "&password="
					+ user.getPassword()
					+ "\"><IMG SRC=\"/rubis/images/bid_now.jpg\" height=22 width=90></a>\n");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>\n");
		}
	}

	void printUserWonItemHeader() {
		printHTML("<br>");
		printHTMLHighlighted("<p><h3>Items you won in the past 30 days.</h3>\n");
		out.println("<TABLE border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Price you bought it<TH>Seller"
				+ "<TBODY>\n");
	}

	void printUserWonItem(Item item) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId() + "\">" + item.getName() + "</a>\n" + "<TD>"
					+ item.getMaxBid() + "\n"
					+ "<TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ item.getSeller().getId() + "\">"
					+ item.getSeller().getNickname() + "</a>\n");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>\n");
		}
	}

	void printUserBoughtItemHeader() {
		printHTML("<br>");
		printHTMLHighlighted("<p><h3>Items you bouhgt in the past 30 days.</h3>\n");
		out.println("<TABLE border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Quantity<TH>Price you bought it<TH>Seller"
				+ "<TBODY>\n");
	}

	void printUserBoughtItem(Item item, Integer qty) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId() + "\">" + item.getName() + "</a>\n" + "<TD>"
					+ qty + "\n" + "<TD>" + item.getBuyNow() + "\n"
					+ "<TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ item.getSeller().getId() + "\">"
					+ item.getSeller().getNickname() + "</a>\n");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>\n");
		}
	}

	void printSellHeader(String title) {
		printHTMLHighlighted("<p><h3>" + title + "</h3>\n");
		out.println("<TABLE id=\"mytable\" border=\"1\" summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Quantity<TH>ReservePrice<TH>Buy Now"
				+ "<TH>Start Date<TH>End Date\n" + "<TBODY>\n");
	}

	void printSell(Item item) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId() + "\">" + item.getName() + "<TD>"
					+ item.getInitialPrice() + "<TD>" + item.getMaxBid()
					+ "<TD>" + item.getQuantity() + "<TD>"
					+ item.getReservePrice() + "<TD>" + item.getBuyNow()
					+ "<TD>" + item.getStartDate() + "<TD>" + item.getEndDate()
					+ "\n");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>\n");
		}
	}

	void printRecommedHeader(String title) {
		printHTMLHighlighted("<p><h3>" + title + "</h3>\n");
		out.println("<TABLE  border=\"1\"  summary=\"List of items\">\n"
				+ "<THEAD>\n"
				+ "<TR><TH>Designation<TH>Initial Price<TH>Current price<TH>Quantity<TH>ReservePrice<TH>Buy Now"
				+ "<TH>Start Date<TH>End Date<TH>Picture<TH>Buy it Now\n"
				+ "<TBODY>\n");
	}

	void printCSS() {
		out.print("<head><link rel=\"Stylesheet\" href=\"css.css\" type=\"text/css\"></head>");
	}

	void printRecommed(Item item) {
		try {

			out.println("<TR><TD><a href=\"/rubis/servlet/ViewItem?itemId="
					+ item.getId()
					+ "\">"
					+ item.getName()
					+ "<TD>"
					+ item.getInitialPrice()
					+ "<TD>"
					+ item.getMaxBid()
					+ "<TD>"
					+ item.getQuantity()
					+ "<TD>"
					+ item.getReservePrice()
					+ "<TD>"
					+ item.getBuyNow()
					+ "<TD>"
					+ item.getStartDate()
					+ "<TD>"
					+ item.getEndDate()
					+ "<TD>"
					+ "<IMG SRC=\"/rubis/images/"
					+ item.getPictures()
					+ "\" height=30 width=130></a>"
					+ "<TD>"
					+ "<p><a href=\"/rubis/servlet/BuyNowAuth?itemId="
					+ item.getId()
					+ "\">"
					+ "<IMG SRC=\"/rubis/images/buy_it_now.jpg\" height=22 width=150></a>"
					+ "<p>\n");
		} catch (Exception e) {
			out.println("Unable to print Item (exception: " + e + ")<br>\n");
		}
	}

	// /////////////////////////////////////
	// Buy now related printed functions //
	// /////////////////////////////////////

	/**
	 * Print the full description of an item and the buy now option
	 * 
	 * @param item
	 *            an <code>Item</code> value
	 * @param userId
	 *            an authenticated user id
	 */
	void printItemDescriptionToBuyNow(Item item, User user) {
		try {
			printHTMLheader("RUBiS: Buy Now");
			printHTMLHighlighted("You are ready to buy this item: "
					+ item.getName());

			out.println("<TABLE>\n"
					+ "<TR><TD>Quantity<TD><b><BIG>"
					+ item.getQuantity()
					+ "</BIG></b>\n"
					+ "<TR><TD>Seller<TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ item.getSeller().getId() + "\">"
					+ item.getSeller().getNickname()
					+ "</a> (<a href=\"/rubis/servlet/PutCommentAuth?to="
					+ item.getSeller().getId() + "&itemId=" + item.getId()
					+ "\">Leave a comment on this user</a>)\n"
					+ "<TR><TD>Started<TD>" + item.getStartDate() + "\n"
					+ "<TR><TD>Ends<TD>" + item.getEndDate() + "\n"
					+ "</TABLE>");

			printHTMLHighlighted("Item description");
			out.println(item.getDescription());
			out.println("<br><p>\n");

			printHTMLHighlighted("Buy Now");
			printHTML("<form action=\"/rubis/servlet/StoreBuyNow\" method=POST>\n"
					+ "<input type=hidden name=userId value="
					+ user.getId()
					+ ">\n"
					+ "<input type=hidden name=itemId value="
					+ item.getId()
					+ ">\n"
					+ "<input type=hidden name=maxQty value="
					+ item.getQuantity() + ">\n");
			if (item.getQuantity().intValue() > 1)
				printHTML("<center><table><tr><td>Quantity:</td>\n"
						+ "<td><input type=text size=5 name=qty></td></tr></table></center>\n");
			else
				printHTML("<input type=hidden name=qty value=1>\n");
			printHTML("<p><center><input type=submit value=\"Buy now!\"></center><p>\n");
		} catch (Exception e) {
			out.println("Unable to print Item description (exception: " + e
					+ ")<br>\n");
		}
	}

	// /////////////////////////////////
	// Bid related printed functions //
	// /////////////////////////////////

	void printBidHistoryHeader() {
		out.println("<TABLE border=\"1\" summary=\"List of bids\">" + "<THEAD>"
				+ "<TR><TH>User ID<TH>Bid amount<TH>Date of bid" + "<TBODY>");
	}

	void printBidHistoryFooter() {
		out.println("</TBODY></TABLE>");
	}

	void printBidHistory(Bid bid) {
		try {
			out.println("<TR><TD><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ bid.getUser().getId() + "\">"
					+ bid.getUser().getNickname() + "<TD>" + bid.getBid()
					+ "<TD>" + bid.getDate());
		} catch (Exception e) {
			out.println("Unable to print Bid (exception: " + e + ")<br>");
		}
	}

	// ///////////////////////////////////////
	// Comment related printed functions //
	// ///////////////////////////////////////

	void printCommentHeader() {
		out.println("<DL>");
	}

	void printComment(Comment comment) {
		try {
			out.println("<DT><b><BIG><a href=\"/rubis/servlet/ViewUserInfo?userId="
					+ comment.getFromUser().getId()
					+ "\">"
					+ comment.getFromUser().getNickname()
					+ "</a></BIG></b>"
					+ " wrote the "
					+ comment.getDate()
					+ "<DD><i>"
					+ comment.getComment() + "</i><p>");
		} catch (Exception e) {
			out.println("Unable to print Comment (exception: " + e + ")<br>");
		}
	}

	void printCommentFooter() {
		out.println("</DL>");
	}

}
