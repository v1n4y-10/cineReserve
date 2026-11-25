
import './MovieCard.css'

function MovieCard({ movie }) {
    return (
        <div className="movie-card">
            <div
                className={`movie-poster ${movie.theme}`}
            >
                <span className="poster-label">CINERESERVE</span>
                <h3>{movie.title}</h3>
                <span className="poster-year">{movie.year}</span>
            </div>

            <div className="movie-info">
                <div>
                    <h3>{movie.title}</h3>
                    <p>{movie.genre}</p>
                </div>

                <span className="movie-rating">
          ★ {movie.rating}
        </span>
            </div>

            <button className="book-btn">
                Book Tickets
            </button>
        </div>
    )
}

export default MovieCard
