import React, { useState, useEffect } from 'react';
import { getCookie } from '../../utils/cookies';
import { useNavigate } from 'react-router-dom';
import './doctors.css';

const DoctorsComponent = () => {
  const [doctorData, setDoctorData] = useState([]);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);




  const fetchDoctorData = async (id) => {
    try {
      const token = getCookie('authToken');
      const t2 = `Bearer ${token.trim()}`;
      console.log(t2);

      const response = await fetch(`http://localhost:8080/api/medical_office/physicians`,
        {method:'GET',
        headers:{
          'Authorization': t2,
          'Content-Type': 'application/json'
          }
        }


      );
      if (!response.ok) {
        throw new Error(`status : ${response.status}`);
      }
      const data = await response.json();
      console.log(data);
      const list = data._embedded.doctorDTOList;

      setDoctorData(list);
    } catch (error) {
      console.error(`Error fetching pacients: ${error.message}`);
      console.log(error);
    }
  };

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
        if (!data.valid) {
          navigate('/'); 
        } else {
          setLoading(false); 
        }
      } else {
        navigate('/'); 
        console.log(response);
      }
    } catch (error) {
      console.error('Error during authentication:', error);
      navigate('/'); 
    }
  };

  useEffect(() => {

    checkAuth();

    const currentId = localStorage.getItem('userId');


    fetchDoctorData(currentId);
  }, [navigate]);



  return (
    
    <div className='doctorsContainer'>

            {doctorData.map(d => (
                <div className='card2' key={d.idDoctor}>

                  <h3>Dr. {d.nume} {d.prenume}</h3>
                  <p>{d.email}</p>
                  <p>{d.phoneNumber}</p>
                  <p>{d.specializare}</p>

                  <div className='actionButtonsContainer'>
                  
                    <button>Get an appointment</button>
                  </div>
                
                </div>
            ))}
      
    </div>
    
  );
};

export default DoctorsComponent;
