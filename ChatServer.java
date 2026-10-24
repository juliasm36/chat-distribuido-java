import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {

    public static Map<String, ClientHandler> usuarios = new HashMap<>();

    private ServerSocket servidor;

    public ChatServer(int porta) {

        try {

            servidor = new ServerSocket(porta);

            System.out.println("Servidor iniciado na porta " + porta);

            while (true) {

                Socket cliente = servidor.accept();

                ClientHandler handler = new ClientHandler(cliente);

                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void enviarParaTodos(String mensagem) {

        synchronized (usuarios) {

            for (ClientHandler cliente : usuarios.values()) {

                cliente.enviarMensagem(mensagem);
            }
        }
    }

    public static void enviarPrivado(
            String destinatario,
            String mensagem) {

        synchronized (usuarios) {

            ClientHandler cliente =
                    usuarios.get(destinatario);

            if (cliente != null) {

                cliente.enviarMensagem(mensagem);
            }
        }
    }

    public static void atualizarListaUsuarios() {

        StringBuilder lista = new StringBuilder("USUARIOS|");

        synchronized (usuarios) {

            for (String nome : usuarios.keySet()) {

                lista.append(nome).append(",");
            }

            for (ClientHandler cliente : usuarios.values()) {

                cliente.enviarMensagem(lista.toString());
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer(8084);
    }
}