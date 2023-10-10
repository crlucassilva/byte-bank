package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDao {

    private Connection conn;

    ContaDao(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {
        String sql = "INSERT INTO tbconta (numero, saldo, cliente_nome, cliente_cpf, cliente_email, esta_ativa) VALUES (?, ?, ?, ?, ?, ?)";
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente, true);

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, conta.getNumero());
            ps.setBigDecimal(2, BigDecimal.ZERO);
            ps.setString(3, cliente.getNome());
            ps.setString(4, cliente.getCpf());
            ps.setString(5, cliente.getEmail());
            ps.setBoolean(6,true);

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {
        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM tbconta WHERE esta_ativa = true";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs  = ps.executeQuery();

            while (rs.next()) {
                Integer numero = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);
                Boolean estaAtiva = rs.getBoolean(6);

                DadosCadastroCliente dados = new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dados);
                contas.add(new Conta(numero, saldo, cliente, estaAtiva));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public Conta buscarContaPorNumero(Integer numero) {
        Conta conta = null;
        String sql = "SELECT * FROM tbconta WHERE numero = ? and esta_ativa = true";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, numero);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer numeroRecuperado = rs.getInt(1);
                BigDecimal saldo = rs.getBigDecimal(2);
                String nome = rs.getString(3);
                String cpf = rs.getString(4);
                String email = rs.getString(5);
                Boolean estaAtiva = rs.getBoolean(6);

                DadosCadastroCliente dados = new DadosCadastroCliente (nome, cpf, email);
                Cliente cliente = new Cliente(dados);

                conta = new Conta(numeroRecuperado, saldo, cliente, estaAtiva);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numeroDaConta, BigDecimal valor) {
        String sql = "UPDATE tbconta SET saldo = ? WHERE numero = ?";

        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, valor);
            ps.setInt(2, numeroDaConta);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public void deletar(Integer numeroDaConta) {
        String sql = "DELETE FROM tbconta WHERE numero = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroDaConta);

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numeroDaConta) {
        String sql = "UPDATE tbconta SET esta_ativa = false WHERE numero = ?";

        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroDaConta);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
}
