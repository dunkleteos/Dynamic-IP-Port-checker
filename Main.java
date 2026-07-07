import javax.swing.SwingUtilities;
public class Main
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            MonitoringGUI gui= new MonitoringGUI();
            gui.setVisible(true);
        });
    }
}
