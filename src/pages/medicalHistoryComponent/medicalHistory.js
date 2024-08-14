import React, { useState, useEffect } from 'react';
import { getCookie } from '../../utils/cookies';
import { useNavigate } from 'react-router-dom';

const MedicalHistoryComponent = () => {
  const [pacientData, setPacientData] = useState([]);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);




  const fetchPacientData = async (id) => {
    try {
      const token = getCookie('authToken');
      const t2 = `Bearer ${token.trim()}`;
      console.log(t2);

      const response = await fetch(`http://localhost:8080/api/medical_office/pacients/${id}/physicians`,
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
      const appData = data._embedded.appointmentResponseList;
      console.log(appData);

      setPacientData(appData);
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
        const appList = data._embedded;
        console.log(appList);
        //console.log(data);
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
            <th>Data</th>
            <th>Nume Doctor</th>
            <th>Prenume Doctor</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
            {pacientData.map(app =>{
                
                const date = new Date(app.data);
                const day = String(date.getDate()).padStart(2, '0');
                const month = String(date.getMonth() + 1).padStart(2, '0'); // Luna este indexată de la 0
                const year = date.getFullYear();
                const hours = String(date.getHours()).padStart(2, '0');
                const minutes = String(date.getMinutes()).padStart(2, '0');

                const formattedDate = `${day}-${month}-${year} ${hours}:${minutes}`;

                return (
                <tr key={`${app.data}-${app.numeDoctor}-${app.prenumeDoctor}`}>
                    <td>{formattedDate}</td>
                    <td>{app.numeDoctor}</td>
                    <td>{app.prenumeDoctor}</td>
                    <td>{app.statusAp}</td>
                </tr>
                )

            })}
            
        </tbody>
      </table>
    </div>
    
  );
};

export default MedicalHistoryComponent;
