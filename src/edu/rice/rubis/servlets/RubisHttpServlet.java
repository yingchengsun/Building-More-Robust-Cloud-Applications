package edu.rice.rubis.servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.Properties;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Provides the method to initialize connection to the database. All the servlets inherit from this class */
public abstract class RubisHttpServlet extends HttpServlet
{

  /** Controls connection pooling */
  private static final boolean enablePooling = true;

  /** Stack of available connections (pool) */
  private Stack freeConnections = null;
  private int poolSize;
  private int currentConn = 0;
  private Properties dbProperties = null;

  public abstract int getPoolSize(); // Get the pool size for this class

  /** Load the driver and get a connection to the database */
  public void init() throws ServletException
  {
    InputStream in = null;
    poolSize = getPoolSize();
    try
    {
      // Get the properties for the database connection
      dbProperties = new Properties();
      in = new FileInputStream(Config.DatabaseProperties);
      dbProperties.load(in);
      // load the driver
      Class.forName(dbProperties.getProperty("datasource.classname"));

      freeConnections = new Stack();
      initializeConnections();
    }
    catch (FileNotFoundException f)
    {
      throw new UnavailableException(
        "Couldn't find file mysql.properties: " + f + "<br>");
    }
    catch (IOException io)
    {
      throw new UnavailableException(
        "Cannot open read mysql.properties: " + io + "<br>");
    }
    catch (ClassNotFoundException c)
    {
      throw new UnavailableException(
        "Couldn't load database driver: " + c + "<br>");
    }
    catch (SQLException s)
    {
      throw new UnavailableException(
        "Couldn't get database connection: " + s + "<br>");
    }
    finally
    {
      try
      {
        if (in != null)
          in.close();
      }
      catch (Exception e)
      {
      }
    }
  }

  /**
   * Initialize the pool of connections to the database.
   * The caller must ensure that the driver has already been
   * loaded else an exception will be thrown.
   *
   * @exception SQLException if an error occurs
   */
  public synchronized void initializeConnections() throws SQLException
  {
    if (enablePooling) 
    {
      for (int i = 0; i < poolSize; i++)
      {
        // Get connections to the database
        freeConnections.push(
          DriverManager.getConnection(
            dbProperties.getProperty("datasource.url"),
            dbProperties.getProperty("datasource.username"),
            dbProperties.getProperty("datasource.password")));
      }
    }
  }

  //  public Connection getConnection()
  //  {
  //    //    currentConn = (currentConn + 1) % poolSize;
  //    //    return conn[currentConn];
  //    try
  //    {
  //      return DriverManager.getConnection(
  //        dbProperties.getProperty("datasource.url"),
  //        dbProperties.getProperty("datasource.username"),
  //        dbProperties.getProperty("datasource.password"));
  //    }
  //    catch (SQLException e)
  //    {
  //      return null;
  //    }
  //
  //  }

  /**
  * Closes a <code>Connection</code>.
  * @param connection to close 
  */
  public void closeConnection(Connection connection)
  {
    try
    {
      connection.close();
    }
    catch (Exception e)
    {

    }
  }

  /**
   * Gets a connection from the pool (round-robin)
   *
   * @return a <code>Connection</code> or 
   * null if no connection is available
   */
  public synchronized Connection getConnection()
  {
    if (enablePooling)
    {
      try
      {
        // Wait for a connection to be available
        while (freeConnections.isEmpty())
        {
          try
          {
            wait();
          }
          catch (InterruptedException e)
          {
            System.out.println("Connection pool wait interrupted.");
          }
        }

        Connection c = (Connection) freeConnections.pop();
        return c;
      }
      catch (EmptyStackException e)
      {
        System.out.println("Out of connections.");
        return null;
      }
    }
    else
    {
      try
      {
        return DriverManager.getConnection(
          dbProperties.getProperty("datasource.url"),
          dbProperties.getProperty("datasource.username"),
          dbProperties.getProperty("datasource.password"));
      } 
      catch (SQLException ex) 
      {
        return null; 
      }
    }
  }

  /**
   * Releases a connection to the pool.
   *
   * @param c the connection to release
   */
  public synchronized void releaseConnection(Connection c)
  {
    if (enablePooling)
    {
      boolean mustNotify = freeConnections.isEmpty();

      freeConnections.push(c);
      // Wake up one servlet waiting for a connection (if any)
      if (mustNotify)
        notifyAll();
    }
    else
    {
      closeConnection(c);
    }
  }

  /**
   * Release all the connections to the database.
   *
   * @exception SQLException if an error occurs
   */
  public synchronized void finalizeConnections() throws SQLException
  {
    if (enablePooling)
    {
      Connection c = null;
      while (!freeConnections.isEmpty())
      {
        c = (Connection) freeConnections.pop();
        c.close();
      }
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {

  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {

  }

  /**
   * Clean up database connections.
   */
  public void destroy()
  {
    try
    {
      finalizeConnections();
    }
    catch (SQLException e)
    {
    }
  }

}
