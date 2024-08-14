import React, { useState, useEffect } from 'react';
import { getCookie } from '../../utils/cookies';
import { useNavigate } from 'react-router-dom';

const PacientInfoComponent = () => {
  const [pacientData, setPacientData] = useState([]);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);




  const fetchPacientData = async (id) => {
    try {
      const token = getCookie('authToken');
      const t2 = `Bearer ${token.trim()}`;
      console.log(t2);

      const response = await fetch(`http://localhost:8080/api/medical_office/pacients/${id}`,
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
      const pacientData = data;

      setPacientData(pacientData);
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


    fetchPacientData(currentId);
  }, [navigate]);

  return (
    
    <div>
      <table>
        <thead>
          <tr>
            <th>Nume</th>
            <th>Prenume</th>
            <th>Email</th>
            <th>Numar de telefon</th>
          </tr>
        </thead>
        <tbody>
            <tr>
              <td>{pacientData.nume}</td>
              <td>{pacientData.prenume}</td>
              <td>{pacientData.email}</td>
              <td>{pacientData.phoneNumber}</td>
            </tr>
        </tbody>
      </table>
    </div>
    
  );
};

export default PacientInfoComponent;
