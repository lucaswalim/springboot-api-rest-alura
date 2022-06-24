package br.com.alura.forum.config.security;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
// Extendemos para indicar ao Spring que essa classe é responsável pela lógica de autenticação
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Ao clicar em Login do Form criado pelo Spring, chegara nesse método
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(username);

        if (usuario.isPresent()) {
            return usuario.get();
        }

        throw new UsernameNotFoundException("Dados inválidos");
    }
}
