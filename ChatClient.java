import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

public class ChatClient extends JFrame {

    private JTextArea areaChat;

    private JTextField campoMensagem;

    private JButton botaoEnviar;
    private JButton botaoPrivado;
    private JButton botaoArquivo;

    private DefaultListModel<String> modeloUsuarios;

    private JList<String> listaUsuarios;

    private BufferedReader entrada;
    private PrintWriter saida;

    public ChatClient() {

        String nickname = JOptionPane.showInputDialog(
                this,
                "Digite seu nickname:");

        try {

            Socket socket = new Socket(
                    "localhost",
                    8084);

            entrada = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            saida = new PrintWriter(
                    socket.getOutputStream(),
                    true);

            saida.println(nickname);

            String resposta = entrada.readLine();

            if ("NICKNAME_EXISTE".equals(resposta)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Nickname já está em uso.");

                System.exit(0);
            }

            criarInterface();

            iniciarRecebimentoMensagens();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao conectar.");
        }
    }

    private void criarInterface() {

        setTitle("Chat Distribuído");

        setSize(800, 500);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        areaChat = new JTextArea();

        areaChat.setEditable(false);

        JScrollPane scrollChat =
                new JScrollPane(areaChat);

        modeloUsuarios = new DefaultListModel<>();

        listaUsuarios = new JList<>(
                modeloUsuarios);

        JScrollPane scrollUsuarios =
                new JScrollPane(listaUsuarios);

        scrollUsuarios.setPreferredSize(
                new Dimension(150, 0));

        campoMensagem = new JTextField();

        botaoEnviar = new JButton(
                "Enviar Público");

        botaoPrivado = new JButton(
                "Enviar Privado");

        botaoArquivo = new JButton(
                "Enviar Arquivo");

        JPanel painelInferior =
                new JPanel(new BorderLayout());

        painelInferior.add(
                campoMensagem,
                BorderLayout.CENTER);

        JPanel painelBotoes =
                new JPanel(new GridLayout(1, 3));

        painelBotoes.add(botaoEnviar);

        painelBotoes.add(botaoPrivado);

        painelBotoes.add(botaoArquivo);

        painelInferior.add(
                painelBotoes,
                BorderLayout.EAST);

        add(scrollChat, BorderLayout.CENTER);

        add(scrollUsuarios, BorderLayout.EAST);

        add(painelInferior, BorderLayout.SOUTH);

        botaoEnviar.addActionListener(
                e -> enviarMensagemPublica());

        botaoPrivado.addActionListener(
                e -> enviarMensagemPrivada());

        botaoArquivo.addActionListener(
                e -> enviarArquivo());

        campoMensagem.addActionListener(
                e -> enviarMensagemPublica());

        setVisible(true);
    }

    private void enviarMensagemPublica() {

        String mensagem =
                campoMensagem.getText();

        if (!mensagem.isBlank()) {

            saida.println(mensagem);

            campoMensagem.setText("");
        }
    }

    private void enviarMensagemPrivada() {

        String usuarioSelecionado =
                listaUsuarios.getSelectedValue();

        if (usuarioSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um usuário.");

            return;
        }

        String mensagem =
                campoMensagem.getText();

        if (!mensagem.isBlank()) {

            saida.println(
                    "/p "
                            + usuarioSelecionado
                            + " "
                            + mensagem);

            areaChat.append(
                    "[PRIVADO para "
                            + usuarioSelecionado
                            + "] "
                            + mensagem
                            + "\n");

            campoMensagem.setText("");
        }
    }

    private void enviarArquivo() {

        String usuarioSelecionado =
                listaUsuarios.getSelectedValue();

        if (usuarioSelecionado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione um usuário.");

            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        int resultado =
                chooser.showOpenDialog(this);

        if (resultado ==
                JFileChooser.APPROVE_OPTION) {

            try {

                File arquivo =
                        chooser.getSelectedFile();

                String conteudo =
                        Files.readString(
                                arquivo.toPath());

                saida.println(
                        "/file "
                                + usuarioSelecionado
                                + " "
                                + arquivo.getName()
                                + " "
                                + conteudo);

                areaChat.append(
                        "[ARQUIVO ENVIADO] "
                                + arquivo.getName()
                                + " para "
                                + usuarioSelecionado
                                + "\n");

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao enviar arquivo.");
            }
        }
    }

    private void atualizarUsuarios(
            String lista) {

        SwingUtilities.invokeLater(() -> {

            modeloUsuarios.clear();

            String[] usuarios =
                    lista.split(",");

            for (String usuario : usuarios) {

                if (!usuario.isBlank()) {

                    modeloUsuarios.addElement(
                            usuario);
                }
            }
        });
    }

    private void iniciarRecebimentoMensagens() {

        Thread thread = new Thread(() -> {

            try {

                String mensagem;

                while ((mensagem =
                        entrada.readLine()) != null) {

                    if (mensagem.startsWith(
                            "USUARIOS|")) {

                        String lista =
                                mensagem.substring(9);

                        atualizarUsuarios(lista);

                    } else if (mensagem.startsWith(
                            "ARQUIVO|")) {

                        String[] partes =
                                mensagem.split("\\|", 4);

                        String remetente =
                                partes[1];

                        String nomeArquivo =
                                partes[2];

                        String conteudo =
                                partes[3];

                        File pasta =
                                new File("recebidos");

                        if (!pasta.exists()) {

                            pasta.mkdir();
                        }

                        PrintWriter writer =
                                new PrintWriter(
                                        "recebidos/"
                                                + nomeArquivo);

                        writer.write(conteudo);

                        writer.close();

                        areaChat.append(
                                "[ARQUIVO RECEBIDO] "
                                        + remetente
                                        + " enviou "
                                        + nomeArquivo
                                        + "\n");

                    } else {

                        areaChat.append(
                                mensagem + "\n");
                    }
                }

            } catch (Exception e) {

                areaChat.append(
                        "Conexão encerrada.\n");
            }
        });

        thread.start();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ChatClient();
        });
    }
}