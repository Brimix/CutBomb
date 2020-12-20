package games.CutBomb;

import games.CutBomb.model.Card;
import games.CutBomb.model.Game;
import games.CutBomb.model.GamePlay;
import games.CutBomb.model.Player;
import games.CutBomb.repository.CardRepository;
import games.CutBomb.repository.GamePlayRepository;
import games.CutBomb.repository.GameRepository;
import games.CutBomb.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class CutBombApplication {
	public static void main(String[] args) {
		SpringApplication.run(CutBombApplication.class, args);
		System.out.println("Server is up!");
	}

	@Bean
	public CommandLineRunner initData(
			PlayerRepository player_rep,
			GameRepository game_rep,
			GamePlayRepository gp_rep,
			CardRepository card_rep
	){ return (args) -> {
		Player BRX = new Player("Brimix", passwordEncoder().encode("secret"));
		Player A = new Player("Anna", passwordEncoder().encode("a"));
		Player B = new Player("Brian", passwordEncoder().encode("b"));
		Player C = new Player("Camila", passwordEncoder().encode("c"));
		Player D = new Player("David", passwordEncoder().encode("d"));
		Player E = new Player("Emilia", passwordEncoder().encode("e"));

		Game G1 = new Game(3);
		Game G2 = new Game(5);

		List<GamePlay> GP = new ArrayList<>();
		GP.add(new GamePlay("Good", A, G1));
		GP.add(new GamePlay("Bad", B, G1));
		GP.add(new GamePlay("Good", C, G1));
		GP.add(new GamePlay("Good", C, G2));
		GP.add(new GamePlay("Bad", D, G2));
		GP.add(new GamePlay("Bad", E, G2));

		List<Card> Deck = new ArrayList<>();
		for(int i = 0; i < 30; i++){
			String type = (i%7 == 0 ? "bomb" : (i%7 == 4 ? "wire" : "blank"));
			Card card = new Card(type, (i < 15 ? G1 : G2));
			GP.get(i/5).addCard(card);
			Deck.add(card);
		}
//		Card C1 = new Card("wire", G1);
//		Card C2 = new Card("wire", G1);
//		Card C3 = new Card("bomb", G1);
//		Card C4 = new Card("bomb", G2);
//		Card C5 = new Card("wire", G2);
//		Card C6 = new Card("wire", G2);

		Random rand = new Random();
		GP.get(rand.nextInt(3)).setCurrent(true);
		GP.get(rand.nextInt(3)+3).setCurrent(true);

		player_rep.saveAll(List.of(BRX, A, B, C, D, E));
		game_rep.saveAll(List.of(G1, G2));
//		gp_rep.saveAll(List.of(GP1, GP2, GP3, GP4, GP5, GP6));
//		card_rep.saveAll(List.of(C1, C2, C3, C4, C5, C6));
		gp_rep.saveAll(GP);
		card_rep.saveAll(Deck);
	};}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUsername(inputName).orElse(null);
			if (player != null) {
				System.out.println("Player found!\n");
				if(player.getUsername() == "Brimix")
					return new User(player.getUsername(), player.getPassword(),
							AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN,USER"));
				else
					return new User(player.getUsername(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
			} else {
				System.out.println("Player not found!\n");
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/admin/**").hasAuthority("ADMIN")
				.antMatchers("/login.html").permitAll()
				.antMatchers("/login.js").permitAll()
				.antMatchers("/jquery.js").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/signup").permitAll()
				.antMatchers("/api/logout").permitAll()
				.antMatchers("/**").hasAuthority("USER");
		http.formLogin() // Don't forget to check for "already logged in"
				.usernameParameter("user")
				.passwordParameter("pass")
				.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}
	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}