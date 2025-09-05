import './App.css';
import { ThemeProvider } from './context/ThemeContext';
import Header from './components/layout/Header';
import Sidebar from './components/layout/Sidebar';
import RightSidebar from './components/layout/RightSidebar';
import TweetCompose from './components/tweet/TweetCompose';
import TweetList from './components/tweet/TweetList';

function App() {
  return (
    <ThemeProvider>
      <div className="min-h-screen bg-gray-100 dark:bg-gray-900 transition-colors">
        <Header />
        
        <div className="max-w-6xl mx-auto flex">
          <Sidebar />
          
          <main className="flex-1 border-l border-r border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 min-h-screen transition-colors">
            <TweetCompose />
            <TweetList />
          </main>
          
          <RightSidebar />
        </div>
      </div>
    </ThemeProvider>
  );
}

export default App;
