import React, { useState, useEffect } from 'react';

const PacientList = () => {
  const [pacients, setPacients] = useState([]);

  const fetchPacients = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/medical_office/pacients',
        {method:'GET',
          headers:{
            'Authorization': "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwidXNlcm5hbWUiOiJwYWNpZW50Iiwicm9sZSI6InBhY2llbnQiLCJleHAiOjE3MjMyMzQ2OTcsImp0aSI6ImNjYzdiYjVlLTg5NjktNGU0MS1hODIxLTJiMmQ5MDc0MGU1OSJ9.5F4Y-Lmt4hqZrLCXc-jXtGIIFe1zOAWw-tQC8lzYbg8",
            'Content-Type': 'application/json'
          }
        }
      );
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      const pacientList = data._embedded.pacientDTOList;
      setPacients(pacientList);
    } catch (error) {
      console.error(`Error fetching pacients: ${error.message}`);
    }
  };

  useEffect(() => {
    fetchPacients();
  }, []);

  return (
    <div>
      <h1>List of Pacients</h1>
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
          {pacients.map(pacient => (
            <tr key={pacient.cnp}>
              <td>{pacient.nume}</td>
              <td>{pacient.prenume}</td>
              <td>{pacient.email}</td>
              <td>{pacient.phoneNumber}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default PacientList;
