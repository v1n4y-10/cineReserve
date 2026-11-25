import './App.css'
import MovieCard from './components/MovieCards'

function App() {
  return (
      <div className="app">
        <nav className="navbar">
          <h1 className="logo">CineReserve<span>.</span></h1>

          <div className="nav-links">
            <a href="/">Home</a>
            <a href="/">Movies</a>
            <a href="/">My Bookings</a>
          </div>

          <div className="nav-actions">
            <button className="login-btn">Login</button>
            <button className="register-btn">Sign Up</button>
          </div>
        </nav>


        <main>
          <section className="hero">
            <div className="hero-content">
              <p className="hero-tag">
                YOUR MOVIE EXPERIENCE STARTS HERE
              </p>

              <h2>
                Movies are better
                <br />
                on the <span>big screen.</span>
              </h2>

              <p className="hero-description">
                Discover movies, explore theatres, and book your
                favourite seats — all in one place.
              </p>

              <button className="explore-btn">
                Explore Movies →
              </button>
            </div>
          </section>

          <section className="movies-section">
            <div className="section-heading">
              <div>
                <p className="hero-tag">FIND YOUR NEXT FAVOURITE</p>
                <h2>Now Showing</h2>
              </div>

              <span>Explore all movies →</span>
            </div>

            <div className="movies-grid">
              {movies.map((movie) => (
                  <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          </section>
        </main>

      </div>
  )
}
const movies = [
  {
    id: 1,
    title: 'Interstellar',
    genre: 'Sci-Fi • Adventure',
    year: 2026,
    rating: '9.0',
    theme: 'poster-purple'
  },
  {
    id: 2,
    title: 'Inception',
    genre: 'Sci-Fi • Thriller',
    year: 2026,
    rating: '8.8',
    theme: 'poster-blue'
  },
  {
    id: 3,
    title: 'The Batman',
    genre: 'Action • Crime',
    year: 2026,
    rating: '8.5',
    theme: 'poster-red'
  },
  {
    id: 4,
    title: 'Dune',
    genre: 'Sci-Fi • Drama',
    year: 2026,
    rating: '8.7',
    theme: 'poster-gold'
  }
]
export default App
