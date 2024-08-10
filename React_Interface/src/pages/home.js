import React, { useState, useEffect } from 'react';
import PacientList from './pacient'; 
import { useNavigate } from 'react-router-dom';
import { getCookie } from '../utils/cookies';

const HomeComponent = () => {

  const navigate = useNavigate();
 
  const [showPacients, setShowPacients] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch('http://localhost:5000/validate', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ authToken: getCookie('authToken') }),
          //credentials: 'include',
        });

        if (response.ok) {
          const data = await response.json();
          console.log(data);
          if (!data.valid) 
            navigate('/'); // Redirect to login if not authenticated
          
        } else {
          navigate('/'); // Redirect to login if response is not okay
          console.log(response);
        }
      } catch (error) {
        console.error('Error during authentication:', error);
        navigate('/'); // Redirect to login on error
      }
    };

    checkAuth();
  }, [navigate]);

  const deleteCookie = (name) => {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/`;
  };

  const handleLogout = async () => {
    try {
      deleteCookie('authToken');
      navigate('/');
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  const handleShowPacientsClick = () => {
    setShowPacients(true);
  };
  

  const handleYourProfileClick = () => {
    navigate('/profile');
  }
  const handleMedicalHistoryClick= () => {
    navigate('/medicalHistory');
  }
  const handleDoctorsClick= () => {
    navigate('/doctors');
  }

  return (
    <div>
      <button onClick={handleLogout}>Logout</button>
      <button onClick={handleYourProfileClick}>Your profile</button>
      <button onClick={handleMedicalHistoryClick}>Medical history</button>
      <button onClick={handleDoctorsClick} >Doctors</button>

      {!showPacients && <button onClick={handleShowPacientsClick}>Show Pacients</button>}
      {showPacients && <PacientList />}
    </div>
  );
};

export default HomeComponent;
