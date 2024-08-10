import React, { useState, useEffect } from 'react';
import { getCookie } from '../utils/cookies';
import { useNavigate } from 'react-router-dom';

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

  const handleHomeClick = () => {
    navigate('/home');
  }

  return (
    
    <div>
      <button onClick={handleHomeClick}>Home</button>
      <h1>Info</h1>
      <table>
        <thead>
          <tr>
            <th>Nume</th>
            <th>Prenume</th>
            <th>Email</th>
            <th>Numar de telefon</th>
            <th>Specializare</th>
          </tr>
        </thead>
        <tbody>
            {doctorData.map(d => (
                <tr key={d.idDoctor}>
                <td>{d.nume}</td>
                <td>{d.prenume}</td>
                <td>{d.email}</td>
                <td>{d.phoneNumber}</td>
                <td>{d.specializare}</td>
                </tr>
            ))}
        </tbody>
      </table>
    </div>
    
  );
};

export default DoctorsComponent;
