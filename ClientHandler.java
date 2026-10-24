import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket cliente;
    private PrintWriter saida;
    private String nickname;

    public ClientHandler(Socket cliente) {
        this.cliente = cliente;
    }

    public void enviarMensagem(String mensagem) {
        saida.println(mensagem);
    }

    @Override
    public void run() {

        try {

            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream()));

            saida = new PrintWriter(
                    cliente.getOutputStream(), true);

            nickname = entrada.readLine();

            synchronized (ChatServer.usuarios) {

                if (ChatServer.usuarios.containsKey(nickname)) {

                    saida.println("NICKNAME_EXISTE");

                    cliente.close();

                    return;
                }

                ChatServer.usuarios.put(nickname, this);
            }

            saida.println("OK");

            System.out.println("Usuário conectado: " + nickname);

            ChatServer.atualizarListaUsuarios();

            String mensagem;

            while ((mensagem = entrada.readLine()) != null) {

                if (mensagem.startsWith("/p ")) {

                    String[] partes =
                            mensagem.split(" ", 3);

                    if (partes.length >= 3) {

                        String destinatario = partes[1];

                        String texto = partes[2];

                        ChatServer.enviarPrivado(
                                destinatario,
                                "[PRIVADO] "
                                        + nickname
                                        + ": "
                                        + texto);
                    }

                } else if (mensagem.startsWith("/file ")) {

                    String[] partes =
                            mensagem.split(" ", 4);

                    if (partes.length >= 4) {

                        String destinatario = partes[1];
                        String nomeArquivo = partes[2];
                        String conteudo = partes[3];

                        ChatServer.enviarPrivado(
                                destinatario,
                                "ARQUIVO|"
                                        + nickname
                                        + "|"
                                        + nomeArquivo
                                        + "|"
                                        + conteudo);
                    }

                } else {

                    System.out.println(
                            nickname + ": " + mensagem);

                    ChatServer.enviarParaTodos(
                            nickname + ": " + mensagem);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Usuário desconectado: " + nickname);

        } finally {

            try {

                if (nickname != null) {

                    synchronized (ChatServer.usuarios) {

                        ChatServer.usuarios.remove(nickname);
                    }

                    ChatServer.atualizarListaUsuarios();
                }

                cliente.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}