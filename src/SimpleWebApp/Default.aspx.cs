using System;

namespace SimpleWebApp
{
    public partial class Default : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            Message.Text = "Server time: " + DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss")
                         + " (CLR " + Environment.Version + ")";
        }
    }
}