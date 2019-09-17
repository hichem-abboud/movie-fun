package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private PlatformTransactionManager platformTransactionManagerMovies;
    @Autowired
    private PlatformTransactionManager platformTransactionManagerAlbums;

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;

    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        TransactionTemplate templateMovie = new TransactionTemplate(platformTransactionManagerMovies);
        System.out.println("isRollbackOnly = " + platformTransactionManagerAlbums.getTransaction(templateMovie).isRollbackOnly());
        templateMovie.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    for (Movie movie : movieFixtures.load()) {

                        moviesBean.addMovie(movie);
                    }
                } catch (Exception e) {

                }
                return null;
            }
        });

        TransactionTemplate templateAlbums = new TransactionTemplate(platformTransactionManagerAlbums);
        templateAlbums.execute(new TransactionCallback<Void>() {

            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    for (Album album : albumFixtures.load()) {
                        albumsBean.addAlbum(album);
                    }
                } catch (Exception e) {

                }

                return null;
            }
        });


        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
