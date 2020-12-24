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
import java.util.Date;
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
		player_rep.saveAll(List.of(BRX, A, B, C, D, E));

		Game G1 = new Game(3);
		Game G2 = new Game(5);
		Game G3 = new Game(5);
		game_rep.saveAll(List.of(G1, G2, G3));

		List<GamePlay> GP = new ArrayList<>();
		GP.add(new GamePlay("Good", A, G1));
		GP.add(new GamePlay("Bad", B, G1));
		GP.add(new GamePlay("Good", C, G1));
		GP.add(new GamePlay("Good", C, G2));
		GP.add(new GamePlay("Bad", D, G2));
		GP.add(new GamePlay("Bad", E, G2));
		Date now = new Date();
		GP.add(new GamePlay(A, G3));
		GP.add(new GamePlay(B, G3)); GP.get(7).setJoined(new Date(now.getTime() + 5000));
		GP.add(new GamePlay(C, G3)); GP.get(8).setJoined(new Date(now.getTime() + 10000));
		GP.add(new GamePlay(D, G3)); GP.get(9).setJoined(new Date(now.getTime() + 15000));
		GP.add(new GamePlay(E, G3)); GP.get(10).setJoined(new Date(now.getTime() + 20000));

		Random rand = new Random();
		GP.get(rand.nextInt(3)).setCurrent(true);
		GP.get(rand.nextInt(3)+3).setCurrent(true);
		gp_rep.saveAll(GP);

		List<Card> Deck = new ArrayList<>();
		for(int i = 0; i < 30; i++){
			String type = (i%7 == 0 ? "bomb" : (i%7 == 4 ? "wire" : "blank"));
			Card card = new Card(type, (i < 15 ? G1 : G2));
			GP.get(i/5).addCard(card);
			Deck.add(card);
		}
		card_rep.saveAll(Deck);
		game_rep.saveAll(List.of(G1, G2, G3));


		Game NG1 = game_rep.findById(1L).get(), NG2 = game_rep.findById(2L).get(), NG3 = game_rep.findById(3L).get();
		GamePlay NGP1 = gp_rep.findById(2L).get(),  NGP2 = gp_rep.findById(4L).get(), NGP3 = gp_rep.findById(7L).get();
		NG1.setHost(NGP1); NG2.setHost(NGP2); NG3.setHost(NGP3);
		game_rep.save(NG1); game_rep.save(NG2); game_rep.save(NG3);
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
				.antMatchers("/js/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/register").permitAll()
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