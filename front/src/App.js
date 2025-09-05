import './App.css';
import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from './context/ThemeContext';
import Header from './components/layout/Header';
import Sidebar from './components/layout/Sidebar';
import RightSidebar from './components/layout/RightSidebar';
import TweetModal from './components/tweet/TweetModal';
import Home from './pages/Home';
import Profile from './pages/Profile';
import Search from './pages/Search';
import Notifications from './pages/Notifications';
import Messages from './pages/Messages';
import Login from './pages/Login';
import Signup from './pages/Signup';

function App() {
  const [isTweetModalOpen, setIsTweetModalOpen] = useState(false);
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false);

  const openTweetModal = () => setIsTweetModalOpen(true);
  const closeTweetModal = () => setIsTweetModalOpen(false);
  
  const openMobileSidebar = () => setIsMobileSidebarOpen(true);
  const closeMobileSidebar = () => setIsMobileSidebarOpen(false);

  return (
    <ThemeProvider>
      <Router>
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900 transition-colors">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/*" element={
              <>
                <Header onMenuClick={openMobileSidebar} />
                
                <div className="max-w-6xl mx-auto flex">
                  <Sidebar 
                    onTweetClick={openTweetModal}
                    isMobileOpen={isMobileSidebarOpen}
                    onMobileClose={closeMobileSidebar}
                  />
                  
                  <main className="flex-1 md:border-l md:border-r border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 min-h-screen transition-colors">
                    <Routes>
                      <Route path="/" element={<Home />} />
                      <Route path="/profile/:username" element={<Profile />} />
                      <Route path="/search" element={<Search />} />
                      <Route path="/notifications" element={<Notifications />} />
                      <Route path="/messages" element={<Messages />} />
                    </Routes>
                  </main>
                  
                  <RightSidebar />
                </div>
                
                <TweetModal isOpen={isTweetModalOpen} onClose={closeTweetModal} />
              </>
            } />
          </Routes>
        </div>
      </Router>
    </ThemeProvider>
  );
}

export default App;
