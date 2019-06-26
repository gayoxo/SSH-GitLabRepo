package fdi.sshrep.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.StreamGobbler;

public class SSHClient {

	public SSHClient(String serverIp,String command, String usernameString,String password) throws IOException{
        System.out.println("inside the ssh function");
        try
        {
            Connection conn = new Connection(serverIp);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(usernameString, password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");        
            ch.ethz.ssh2.Session sess = conn.openSession();
            sess.execCommand(command);  
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            System.out.println("the output of the command is");
            
            StringBuffer S=new StringBuffer();
            
            while (true)
            {
                String line = br.readLine();
                if (line == null)
                    break;
                S.append(line);
            }
            
            
//            System.out.println(S.toString());
            System.out.println();
            System.out.println("///REPOLIST///");
            
            JSONArray obj = new JSONArray(S.toString());
		   for (int i = 0; i < obj.length(); i++) {
			JSONObject Repo=obj.getJSONObject(i);
			String n1 = Repo.getString("name");
			String n2 = Repo.getString("ssh_url_to_repo");
			System.out.println(n1+"->"+n2);
		} 
		   System.out.println();
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close();
            conn.close();
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);

        }
    }
	

	public static void main(String[] args) {
		
		if (args.length<5)
			{
			System.err.println("Argument error: It should be <sshServer> <PRIVATE-TOKEN> <gitServer> <sshUser> <sshPassword>");
			System.exit(-1);
			}
			
		try {
			new SSHClient(args[0],"curl --request GET --header \"PRIVATE-TOKEN: "+args[1]+"\" "+args[2]+"/api/v4/projects",args[3],args[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
