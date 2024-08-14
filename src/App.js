import logo from './logo.svg';
import './App.css';
import HomeComponent from './pages/homeComponent/home';
import LoginComponent from './pages/loginComponent/login';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function App() {
  return (
    <Router>
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginComponent />} /> 
        <Route path="/home" element={<HomeComponent />} /> 
        


        
      </Routes>
    </div>
    </Router>
  );
}

export default App;
