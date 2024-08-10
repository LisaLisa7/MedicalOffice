import logo from './logo.svg';
import './App.css';
import HomeComponent from './pages/home';
import LoginComponent from './pages/login';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import PacientInfoComponent from './pages/pacientInfo';
import MedicalHistoryComponent from './pages/medicalHistory';
import DoctorsComponent from './pages/doctors';


function App() {
  return (
    <Router>
    <div className="App">
      <Routes>
        <Route path="/" element={<LoginComponent />} /> 
        <Route path="/home" element={<HomeComponent />} /> 
        <Route path="/profile" element={<PacientInfoComponent />} />
        <Route path="/medicalHistory" element={<MedicalHistoryComponent/>} />
        <Route path="/doctors" element={<DoctorsComponent/>} />


        
      </Routes>
    </div>
    </Router>
  );
}

export default App;
