import React, { useState, useEffect } from 'react';
import { 
  Upload, 
  FileText, 
  CheckCircle, 
  AlertTriangle, 
  Lightbulb, 
  Sparkles, 
  Check, 
  X, 
  Briefcase,
  GraduationCap,
  Award,
  Layers
} from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import AtsScoreGauge from '../components/AtsScoreGauge';

const ResumeUpload = () => {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [analysis, setAnalysis] = useState(null);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const [isDragging, setIsDragging] = useState(false);

  useEffect(() => {
    // Try to load latest ATS score & resume info if previously uploaded
    const fetchLatest = async () => {
      try {
        const atsRes = await api.get(API_ENDPOINTS.ATS_LATEST);
        const atsData = atsRes.data?.data || atsRes.data;
        if (atsData && (atsData.atsScore !== undefined || atsData.skills)) {
          setAnalysis({
            atsScore: atsData.atsScore || 75,
            strengths: Array.isArray(atsData.strengths) ? atsData.strengths : [],
            weaknesses: Array.isArray(atsData.weaknesses) ? atsData.weaknesses : [],
            missingSections: Array.isArray(atsData.missingSections) ? atsData.missingSections : (atsData.missingSkills || []),
            suggestions: Array.isArray(atsData.suggestions) ? atsData.suggestions : [],
            detectedKeywords: atsData.detectedKeywords || atsData.skills || [],
            parsedSkills: atsData.parsedSkills || (Array.isArray(atsData.skills) ? atsData.skills.join(', ') : ''),
            educationDetails: atsData.detectedEducation || atsData.educationDetails,
            graduationYear: atsData.detectedGraduationYear || atsData.graduationYear,
            experienceYears: atsData.experienceYears || 0
          });
        }
      } catch (ignored) {}
    };
    fetchLatest();
  }, []);

  const validateAndSetFile = (selected) => {
    if (!selected) return;
    const name = selected.name.toLowerCase();
    if (!name.endsWith('.pdf') && !name.endsWith('.docx')) {
      setError('Please upload a PDF or DOCX resume (.pdf or .docx)');
      return;
    }
    if (selected.size > 10 * 1024 * 1024) {
      setError('Resume file must be 10MB or smaller');
      return;
    }
    setFile(selected);
    setError('');
  };

  const handleFileChange = (e) => {
    validateAndSetFile(e.target.files[0]);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setError('Please select a PDF or DOCX file first');
      return;
    }

    setError('');
    setSuccessMsg('');
    setUploading(true);

    const formData = new FormData();
    formData.append('file', file);
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        const u = JSON.parse(storedUser);
        if (u.id) formData.append('userId', u.id);
      } catch (ignored) {}
    }

    try {
      const res = await api.post(API_ENDPOINTS.RESUME_UPLOAD, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      const data = res.data?.data || res.data;
      if (data) {
        setAnalysis({
          atsScore: data.atsScore || 75,
          strengths: Array.isArray(data.strengths) ? data.strengths : [],
          weaknesses: Array.isArray(data.weaknesses) ? data.weaknesses : [],
          missingSections: Array.isArray(data.missingSections) ? data.missingSections : (data.missingSkills || []),
          suggestions: Array.isArray(data.suggestions) ? data.suggestions : [],
          detectedKeywords: data.detectedKeywords || data.skills || [],
          parsedSkills: data.parsedSkills || (Array.isArray(data.skills) ? data.skills.join(', ') : ''),
          educationDetails: data.detectedEducation || data.educationDetails,
          graduationYear: data.detectedGraduationYear || data.graduationYear,
          experienceYears: data.experienceYears || 0
        });
        setSuccessMsg('Resume parsed, analyzed, and scored successfully!');
        localStorage.setItem('hasResume', 'true');
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to upload and parse resume. Please check format.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem', maxWidth: '1000px', margin: '0 auto' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Resume Parsing & ATS Evaluation Center
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Upload your PDF or DOCX resume to benchmark against enterprise applicant tracking systems
        </p>
      </div>

      {/* Upload Drop Zone Card */}
      <div className="card">
        <form onSubmit={handleUpload}>
          <div 
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            style={{
              border: isDragging ? '2px dashed var(--primary)' : '2px dashed var(--border-color)',
              borderRadius: 'var(--radius-lg)',
              padding: '2.5rem 1.5rem',
              textAlign: 'center',
              backgroundColor: isDragging ? 'var(--primary-light)' : 'rgba(26, 34, 52, 0.4)',
              cursor: 'pointer',
              transition: 'all 0.2s ease',
            }}
          >
            <input 
              type="file" 
              id="resume-input"
              accept=".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
              onChange={handleFileChange}
              style={{ display: 'none' }}
            />
            <label htmlFor="resume-input" style={{ cursor: 'pointer', display: 'block' }}>
              <div style={{
                width: '60px', height: '60px', borderRadius: '50%',
                backgroundColor: 'var(--primary-light)', color: 'var(--primary)',
                display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem'
              }}>
                <Upload size={28} />
              </div>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.35rem' }}>
                {file ? file.name : 'Click to Browse or Drag & Drop PDF / DOCX Resume'}
              </h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                PDF and DOCX supported • Maximum size 10MB • Explainable 0–100 ATS scoring
              </p>
            </label>
          </div>

          {error && (
            <div style={{
              marginTop: '1rem', padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--danger-bg)', color: 'var(--danger)', fontSize: '0.85rem'
            }}>
              {error}
            </div>
          )}

          {successMsg && (
            <div style={{
              marginTop: '1rem', padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--success-bg)', color: 'var(--success)', fontSize: '0.85rem',
              display: 'flex', alignItems: 'center', justifyContent: 'space-between'
            }}>
              <div>
                <CheckCircle size={16} style={{ marginRight: '6px', verticalAlign: 'middle' }} />
                {successMsg}
              </div>
              <a href="/matches" className="btn btn-sm btn-secondary" style={{ marginLeft: '1rem' }}>
                View Matches →
              </a>
            </div>
          )}

          <div style={{ marginTop: '1.25rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
            {analysis && (
              <a href="/matches" className="btn btn-secondary" style={{ padding: '0.75rem 1.25rem' }}>
                <Sparkles size={16} /> Explore AI Matches
              </a>
            )}
            <button
              type="submit"
              className="btn btn-primary"
              disabled={!file || uploading}
              style={{ padding: '0.75rem 1.75rem', marginLeft: 'auto' }}
            >
              <Sparkles size={18} />
              <span>{uploading ? 'Analyzing Document & Scoring...' : 'Upload & Analyze Resume'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Analysis Results Display */}
      {analysis && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {/* Top Score Card */}
          <div className="card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '2rem' }}>
            <div style={{ flex: 1, minWidth: '260px' }}>
              <div className="badge badge-primary" style={{ marginBottom: '0.75rem' }}>
                ATS Evaluation Report
              </div>
              <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.5rem' }}>
                {analysis.extractedName ? `${analysis.extractedName}'s Resume Score` : 'Resume ATS Score'}
              </h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Evaluated against industry standards for formatting readability, technical keyword depth, educational verification, and measurable achievements.
              </p>
            </div>
            <AtsScoreGauge score={analysis.atsScore} size={160} />
          </div>

          {/* Extracted Details & Skills */}
          <div className="grid-2">
            {/* Extracted Skills */}
            <div className="card">
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Layers size={18} color="var(--primary)" /> Extracted Technical Skills
              </h3>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                {analysis.extractedSkills && analysis.extractedSkills.length > 0 ? (
                  analysis.extractedSkills.map((sk, idx) => (
                    <span key={idx} className="skill-chip matched">{sk}</span>
                  ))
                ) : analysis.detectedKeywords && analysis.detectedKeywords.length > 0 ? (
                  analysis.detectedKeywords.map((sk, idx) => (
                    <span key={idx} className="skill-chip matched">{sk}</span>
                  ))
                ) : (
                  <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No explicit technical skills identified.</span>
                )}
              </div>
            </div>

            {/* Extracted Metadata (Education, Batch, Experience) */}
            <div className="card">
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <GraduationCap size={18} color="var(--secondary)" /> Profile Data Extraction
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.875rem' }}>
                <div>
                  <span style={{ color: 'var(--text-muted)' }}>Detected Education: </span>
                  <strong>{analysis.educationDetails || 'Engineering Degree'}</strong>
                </div>
                <div>
                  <span style={{ color: 'var(--text-muted)' }}>Detected Graduation Batch: </span>
                  <strong>{analysis.graduationYear ? `Class of ${analysis.graduationYear}` : 'Not detected'}</strong>
                </div>
                <div>
                  <span style={{ color: 'var(--text-muted)' }}>Estimated Experience: </span>
                  <strong>{analysis.experienceYears != null ? `${analysis.experienceYears} Years` : 'Fresher'}</strong>
                </div>
              </div>
            </div>
          </div>

          {/* Strengths & Weaknesses Breakdown */}
          <div className="grid-2">
            {/* Strengths */}
            <div className="card">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem', color: '#34d399', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Check size={18} /> ATS Strengths
              </h3>
              <ul style={{ paddingLeft: '1.25rem', fontSize: '0.875rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {analysis.strengths && analysis.strengths.map((st, i) => (
                  <li key={i}>{st}</li>
                ))}
              </ul>
            </div>

            {/* Weaknesses / Missing Sections */}
            <div className="card">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '1rem', color: '#fb7185', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <AlertTriangle size={18} /> Missing Areas & Flags
              </h3>
              <ul style={{ paddingLeft: '1.25rem', fontSize: '0.875rem', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {analysis.weaknesses && analysis.weaknesses.map((w, i) => (
                  <li key={i}>{w}</li>
                ))}
                {analysis.missingSections && analysis.missingSections.map((m, i) => (
                  <li key={i}>Missing section header: <strong>{m}</strong></li>
                ))}
              </ul>
            </div>
          </div>

          {/* Actionable Improvement Suggestions */}
          <div className="card" style={{ backgroundColor: '#131c2e', borderColor: '#263554' }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '1rem', color: '#f59e0b', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Lightbulb size={20} /> Actionable ATS Improvement Suggestions
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {analysis.suggestions && analysis.suggestions.map((sug, idx) => (
                <div key={idx} style={{
                  padding: '0.75rem 1rem',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: '#1a253c',
                  border: '1px solid #283756',
                  fontSize: '0.875rem',
                  color: '#e2e8f0',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.75rem'
                }}>
                  <span style={{ fontWeight: 800, color: '#f59e0b' }}>{idx + 1}.</span>
                  <span>{sug}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResumeUpload;
