import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCookie } from '../../utils/cookies';

import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';

import LocalHospitalIcon from '@mui/icons-material/LocalHospital';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import LogoutIcon from '@mui/icons-material/Logout';
import PeopleIcon from '@mui/icons-material/People';
import DoctorsComponent from '../doctorsComponent/doctors';
import MedicalHistoryComponent from '../medicalHistoryComponent/medicalHistory';
import PacientInfoComponent from '../pacientInfoComponent/pacientInfo';
import Box from '@mui/material/Box';

const HomeComponent = () => {

  const navigate = useNavigate();
  const [value, setValue] = React.useState(0);
 
  //const [showPacients, setShowPacients] = useState(false);

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
            navigate('/'); 
          
        } else {
          navigate('/'); 
          console.log(response);
        }
      } catch (error) {
        console.error('Error during authentication:', error);
        navigate('/'); 
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


  return (
    <div>
      <BottomNavigation
        showLabels
        value={value}
        onChange={(event, newValue) => {
          setValue(newValue);
        }}
      >
        <BottomNavigationAction  label="Doctors" icon={<PeopleIcon />} />
        <BottomNavigationAction  label="Medical History" icon={<LocalHospitalIcon />} />
      
        <BottomNavigationAction  label="Your profile" icon={<AccountBoxIcon />} />
        <BottomNavigationAction onClick={handleLogout} label="Logout" icon={<LogoutIcon />} />
        

      </BottomNavigation>


      <Box sx={{ mt: 2 }}>
        {value === 0 && <DoctorsComponent />}
        {value === 1 && <MedicalHistoryComponent />}
        {value === 2 && <PacientInfoComponent/>}
      </Box>

    </div>
  );
};

export default HomeComponent;
