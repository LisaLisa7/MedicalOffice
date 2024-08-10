import React, { useState, useEffect  } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCookie } from '../utils/cookies';
import { jwtDecode } from 'jwt-decode';


const LoginComponent = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message,setMessage] = useState('');

  const navigate = useNavigate();

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
          if (data.valid) {
            navigate('/home'); 
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

    checkAuth();
  }, [navigate]);


  const handleLogin = async () => {
    try {
      const response = await fetch('http://localhost:5000/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username,
          password,
        }),
      });

      if (response.ok) {
        const data = await response.json();
        const token = data.token;
        console.log('Token:', token);


        document.cookie = `authToken=${token}; path=/;`;

        const decodedToken = jwtDecode(token);
        const userId = decodedToken.sub;
        const username = decodedToken.username;

        console.log('User ID:', userId);
        console.log('Username:', username);

        localStorage.setItem('userId', userId);
        localStorage.setItem('username', username);

        const tokennnn = getCookie('authToken');
        //console.log('Tokennn:', tokennnn); // Should log the token value if present

        //setMessage("all good");

        navigate('/home'); 


      } else {
        console.error('Authentication failed');
        setMessage("wrong creds")
      }
    } catch (error) {
      console.error('Error during authentication:', error);
    }
  };

  return (
    <div>
      <label>Username:</label>
      <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
      <br />
      <label>Password:</label>
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      <br />
      <button onClick={handleLogin}>Login</button>

      {message && <p>{message}</p>}
      
    </div>
  );
};

export default LoginComponent;