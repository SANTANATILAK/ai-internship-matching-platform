import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Briefcase, 
  FileText, 
  Target, 
  Bookmark, 
  Send, 
  User, 
  ShieldCheck, 
  LogOut, 
  Layers, 
  CheckCircle,
  Bell
} from 'lucide-react';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isAdmin = user && user.role === 'ADMIN';

  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand">
        <div style={{
          width: '38px', 
          height: '38px', 
          borderRadius: '10px', 
          background: 'linear-gradient(135deg, #6366f1, #8b5cf6)', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          color: '#fff'
        }}>
          <Briefcase size={22} />
        </div>
        <span>Intern<span className="brand-badge">Match</span></span>
      </Link>

      <ul className="nav-links">
        {user ? (
          isAdmin ? (
            <>
              <li>
                <NavLink to="/admin" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  <ShieldCheck size={17} style={{ marginRight: '6px', verticalAlign: 'middle' }} />
                  Overview
                </NavLink>
              </li>
              <li>
                <NavLink to="/admin/companies" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Companies
                </NavLink>
              </li>
              <li>
                <NavLink to="/admin/opportunities" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Opportunities
                </NavLink>
              </li>
              <li>
                <NavLink to="/admin/verification" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Verification
                </NavLink>
              </li>
            </>
          ) : (
            <>
              <li>
                <NavLink to="/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Dashboard
                </NavLink>
              </li>
              <li>
                <NavLink to="/resume" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Resume & ATS
                </NavLink>
              </li>
              <li>
                <NavLink to="/matches" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  AI Matches
                </NavLink>
              </li>
              <li>
                <NavLink to="/opportunities" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Opportunities
                </NavLink>
              </li>
              <li>
                <NavLink to="/saved" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Saved
                </NavLink>
              </li>
              <li>
                <NavLink to="/applications" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Applications
                </NavLink>
              </li>
            </>
          )
        ) : (
          <>
            <li><NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>Home</NavLink></li>
            <li><NavLink to="/opportunities" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>Browse Jobs</NavLink></li>
          </>
        )}
      </ul>

      <div className="nav-actions">
        {user ? (
          <>
            {!isAdmin && (
              <NavLink to="/notifications" className="btn btn-secondary btn-sm" style={{ padding: '0.5rem' }}>
                <Bell size={18} />
              </NavLink>
            )}
            {!isAdmin && (
              <NavLink to="/profile" className="btn btn-secondary btn-sm">
                <User size={16} />
                <span>{user.name?.split(' ')[0]}</span>
              </NavLink>
            )}
            <button onClick={handleLogout} className="btn btn-danger btn-sm">
              <LogOut size={16} />
              <span>Sign Out</span>
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn btn-secondary btn-sm">Sign In</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
