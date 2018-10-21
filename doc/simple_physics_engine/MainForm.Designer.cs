namespace code
{
	partial class MainForm
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.checkAddShape = new System.Windows.Forms.CheckBox();
			this.buttonStack = new System.Windows.Forms.Button();
			this.textBoxStack = new System.Windows.Forms.TextBox();
			this.buttonWall = new System.Windows.Forms.Button();
			this.textBoxWallX = new System.Windows.Forms.TextBox();
			this.textBoxWallY = new System.Windows.Forms.TextBox();
			this.checkBoxRun = new System.Windows.Forms.CheckBox();
			this.buttonStep = new System.Windows.Forms.Button();
			this.buttonPrevStep = new System.Windows.Forms.Button();
			this.buttonShootBall = new System.Windows.Forms.Button();
			this.checkBoxDebugDraw = new System.Windows.Forms.CheckBox();
			this.buttonSeaSaw = new System.Windows.Forms.Button();
			this.buttonCyclinder = new System.Windows.Forms.Button();
			this.buttonCircle = new System.Windows.Forms.Button();
			this.buttonLine = new System.Windows.Forms.Button();
			this.buttonRectPlane = new System.Windows.Forms.Button();
			this.buttonPoint = new System.Windows.Forms.Button();
			this.buttonSphere = new System.Windows.Forms.Button();
			this.buttonFootball = new System.Windows.Forms.Button();
			this.buttonCube = new System.Windows.Forms.Button();
			this.buttonEllipsoid = new System.Windows.Forms.Button();
			this.pictureBox = new System.Windows.Forms.PictureBox();
			this.buttonBowling = new System.Windows.Forms.Button();
			this.buttonMarbles = new System.Windows.Forms.Button();
			this.buttonDominos = new System.Windows.Forms.Button();
			((System.ComponentModel.ISupportInitialize)(this.pictureBox)).BeginInit();
			this.SuspendLayout();
			// 
			// checkAddShape
			// 
			this.checkAddShape.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
			this.checkAddShape.AutoSize = true;
			this.checkAddShape.Location = new System.Drawing.Point(427, 593);
			this.checkAddShape.Name = "checkAddShape";
			this.checkAddShape.Size = new System.Drawing.Size(96, 17);
			this.checkAddShape.TabIndex = 10;
			this.checkAddShape.Text = "Add RigidBody";
			this.checkAddShape.UseVisualStyleBackColor = true;
			this.checkAddShape.CheckedChanged += new System.EventHandler(this.checkAddShape_CheckedChanged);
			// 
			// buttonStack
			// 
			this.buttonStack.Location = new System.Drawing.Point(4, 593);
			this.buttonStack.Name = "buttonStack";
			this.buttonStack.Size = new System.Drawing.Size(50, 23);
			this.buttonStack.TabIndex = 11;
			this.buttonStack.Text = "Stack";
			this.buttonStack.UseVisualStyleBackColor = true;
			this.buttonStack.Click += new System.EventHandler(this.buttonStack_Click);
			// 
			// textBoxStack
			// 
			this.textBoxStack.Location = new System.Drawing.Point(4, 618);
			this.textBoxStack.Name = "textBoxStack";
			this.textBoxStack.Size = new System.Drawing.Size(26, 20);
			this.textBoxStack.TabIndex = 12;
			// 
			// buttonWall
			// 
			this.buttonWall.Location = new System.Drawing.Point(63, 593);
			this.buttonWall.Name = "buttonWall";
			this.buttonWall.Size = new System.Drawing.Size(72, 23);
			this.buttonWall.TabIndex = 13;
			this.buttonWall.Text = "Wall";
			this.buttonWall.UseVisualStyleBackColor = true;
			this.buttonWall.Click += new System.EventHandler(this.buttonWall_Click);
			// 
			// textBoxWallX
			// 
			this.textBoxWallX.Location = new System.Drawing.Point(63, 619);
			this.textBoxWallX.Name = "textBoxWallX";
			this.textBoxWallX.Size = new System.Drawing.Size(32, 20);
			this.textBoxWallX.TabIndex = 14;
			// 
			// textBoxWallY
			// 
			this.textBoxWallY.Location = new System.Drawing.Point(101, 619);
			this.textBoxWallY.Name = "textBoxWallY";
			this.textBoxWallY.Size = new System.Drawing.Size(34, 20);
			this.textBoxWallY.TabIndex = 15;
			// 
			// checkBoxRun
			// 
			this.checkBoxRun.AutoSize = true;
			this.checkBoxRun.Location = new System.Drawing.Point(212, 624);
			this.checkBoxRun.Name = "checkBoxRun";
			this.checkBoxRun.Size = new System.Drawing.Size(97, 17);
			this.checkBoxRun.TabIndex = 16;
			this.checkBoxRun.Text = "Run Simulation";
			this.checkBoxRun.UseVisualStyleBackColor = true;
			// 
			// buttonStep
			// 
			this.buttonStep.Location = new System.Drawing.Point(212, 594);
			this.buttonStep.Name = "buttonStep";
			this.buttonStep.Size = new System.Drawing.Size(75, 24);
			this.buttonStep.TabIndex = 17;
			this.buttonStep.Text = "Single Step";
			this.buttonStep.UseVisualStyleBackColor = true;
			this.buttonStep.Click += new System.EventHandler(this.buttonStep_Click);
			// 
			// buttonPrevStep
			// 
			this.buttonPrevStep.Location = new System.Drawing.Point(294, 594);
			this.buttonPrevStep.Name = "buttonPrevStep";
			this.buttonPrevStep.Size = new System.Drawing.Size(75, 23);
			this.buttonPrevStep.TabIndex = 18;
			this.buttonPrevStep.Text = "Prev Step";
			this.buttonPrevStep.UseVisualStyleBackColor = true;
			this.buttonPrevStep.Click += new System.EventHandler(this.buttonPrevStep_Click);
			// 
			// buttonShootBall
			// 
			this.buttonShootBall.Location = new System.Drawing.Point(141, 594);
			this.buttonShootBall.Name = "buttonShootBall";
			this.buttonShootBall.Size = new System.Drawing.Size(64, 23);
			this.buttonShootBall.TabIndex = 19;
			this.buttonShootBall.Text = "Shoot Ball";
			this.buttonShootBall.UseVisualStyleBackColor = true;
			this.buttonShootBall.Click += new System.EventHandler(this.buttonShootBall_Click);
			// 
			// checkBoxDebugDraw
			// 
			this.checkBoxDebugDraw.AutoSize = true;
			this.checkBoxDebugDraw.Checked = true;
			this.checkBoxDebugDraw.CheckState = System.Windows.Forms.CheckState.Checked;
			this.checkBoxDebugDraw.Location = new System.Drawing.Point(427, 616);
			this.checkBoxDebugDraw.Name = "checkBoxDebugDraw";
			this.checkBoxDebugDraw.Size = new System.Drawing.Size(86, 17);
			this.checkBoxDebugDraw.TabIndex = 20;
			this.checkBoxDebugDraw.Text = "Debug Draw";
			this.checkBoxDebugDraw.UseVisualStyleBackColor = true;
			// 
			// buttonSeaSaw
			// 
			this.buttonSeaSaw.Image = global::code.Resource.seasaw;
			this.buttonSeaSaw.Location = new System.Drawing.Point(528, 629);
			this.buttonSeaSaw.Name = "buttonSeaSaw";
			this.buttonSeaSaw.Size = new System.Drawing.Size(32, 32);
			this.buttonSeaSaw.TabIndex = 21;
			this.buttonSeaSaw.UseVisualStyleBackColor = true;
			this.buttonSeaSaw.Click += new System.EventHandler(this.buttonSeaSaw_Click);
			// 
			// buttonCyclinder
			// 
			this.buttonCyclinder.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonCyclinder.BackgroundImage = global::code.Resource.cyclinder;
			this.buttonCyclinder.Location = new System.Drawing.Point(529, 593);
			this.buttonCyclinder.Name = "buttonCyclinder";
			this.buttonCyclinder.Size = new System.Drawing.Size(32, 32);
			this.buttonCyclinder.TabIndex = 9;
			this.buttonCyclinder.UseVisualStyleBackColor = true;
			this.buttonCyclinder.Click += new System.EventHandler(this.buttonCyclinder_Click);
			// 
			// buttonCircle
			// 
			this.buttonCircle.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonCircle.BackgroundImage = global::code.Resource.circle;
			this.buttonCircle.Location = new System.Drawing.Point(567, 593);
			this.buttonCircle.Name = "buttonCircle";
			this.buttonCircle.Size = new System.Drawing.Size(32, 32);
			this.buttonCircle.TabIndex = 8;
			this.buttonCircle.UseVisualStyleBackColor = true;
			this.buttonCircle.Click += new System.EventHandler(this.buttonCircle_Click);
			// 
			// buttonLine
			// 
			this.buttonLine.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonLine.BackgroundImage = global::code.Resource.line;
			this.buttonLine.Location = new System.Drawing.Point(605, 593);
			this.buttonLine.Name = "buttonLine";
			this.buttonLine.Size = new System.Drawing.Size(32, 32);
			this.buttonLine.TabIndex = 7;
			this.buttonLine.UseVisualStyleBackColor = true;
			this.buttonLine.Click += new System.EventHandler(this.buttonLine_Click);
			// 
			// buttonRectPlane
			// 
			this.buttonRectPlane.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonRectPlane.BackgroundImage = global::code.Resource.rectplane;
			this.buttonRectPlane.Location = new System.Drawing.Point(643, 593);
			this.buttonRectPlane.Name = "buttonRectPlane";
			this.buttonRectPlane.Size = new System.Drawing.Size(32, 32);
			this.buttonRectPlane.TabIndex = 6;
			this.buttonRectPlane.UseVisualStyleBackColor = true;
			this.buttonRectPlane.Click += new System.EventHandler(this.buttonRectPlane_Click);
			// 
			// buttonPoint
			// 
			this.buttonPoint.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonPoint.BackgroundImage = global::code.Resource.point;
			this.buttonPoint.Location = new System.Drawing.Point(681, 593);
			this.buttonPoint.Name = "buttonPoint";
			this.buttonPoint.Size = new System.Drawing.Size(32, 32);
			this.buttonPoint.TabIndex = 5;
			this.buttonPoint.UseVisualStyleBackColor = true;
			this.buttonPoint.Click += new System.EventHandler(this.buttonPoint_Click);
			// 
			// buttonSphere
			// 
			this.buttonSphere.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonSphere.BackgroundImage = global::code.Resource.sphere;
			this.buttonSphere.Location = new System.Drawing.Point(719, 593);
			this.buttonSphere.Name = "buttonSphere";
			this.buttonSphere.Size = new System.Drawing.Size(32, 32);
			this.buttonSphere.TabIndex = 4;
			this.buttonSphere.UseVisualStyleBackColor = true;
			this.buttonSphere.Click += new System.EventHandler(this.buttonSphere_Click);
			// 
			// buttonFootball
			// 
			this.buttonFootball.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonFootball.BackgroundImage = global::code.Resource.football;
			this.buttonFootball.Location = new System.Drawing.Point(758, 593);
			this.buttonFootball.Name = "buttonFootball";
			this.buttonFootball.Size = new System.Drawing.Size(32, 32);
			this.buttonFootball.TabIndex = 3;
			this.buttonFootball.UseVisualStyleBackColor = true;
			this.buttonFootball.Click += new System.EventHandler(this.buttonFootball_Click);
			// 
			// buttonCube
			// 
			this.buttonCube.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonCube.BackgroundImage = global::code.Resource.cube;
			this.buttonCube.Location = new System.Drawing.Point(797, 593);
			this.buttonCube.Name = "buttonCube";
			this.buttonCube.Size = new System.Drawing.Size(32, 32);
			this.buttonCube.TabIndex = 2;
			this.buttonCube.UseVisualStyleBackColor = true;
			this.buttonCube.Click += new System.EventHandler(this.buttonCube_Click);
			// 
			// buttonEllipsoid
			// 
			this.buttonEllipsoid.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.buttonEllipsoid.BackgroundImage = global::code.Resource.ellipsoid;
			this.buttonEllipsoid.Location = new System.Drawing.Point(837, 593);
			this.buttonEllipsoid.Name = "buttonEllipsoid";
			this.buttonEllipsoid.Size = new System.Drawing.Size(32, 32);
			this.buttonEllipsoid.TabIndex = 1;
			this.buttonEllipsoid.UseVisualStyleBackColor = true;
			this.buttonEllipsoid.Click += new System.EventHandler(this.buttonEllipsoid_Click);
			// 
			// pictureBox
			// 
			this.pictureBox.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
						| System.Windows.Forms.AnchorStyles.Left)
						| System.Windows.Forms.AnchorStyles.Right)));
			this.pictureBox.Location = new System.Drawing.Point(1, 1);
			this.pictureBox.Name = "pictureBox";
			this.pictureBox.Size = new System.Drawing.Size(870, 586);
			this.pictureBox.TabIndex = 0;
			this.pictureBox.TabStop = false;
			// 
			// buttonBowling
			// 
			this.buttonBowling.Image = global::code.Resource.bowling;
			this.buttonBowling.Location = new System.Drawing.Point(567, 628);
			this.buttonBowling.Name = "buttonBowling";
			this.buttonBowling.Size = new System.Drawing.Size(32, 32);
			this.buttonBowling.TabIndex = 22;
			this.buttonBowling.UseVisualStyleBackColor = true;
			this.buttonBowling.Click += new System.EventHandler(this.buttonBowling_Click);
			// 
			// buttonMarbles
			// 
			this.buttonMarbles.Image = global::code.Resource.marbles;
			this.buttonMarbles.Location = new System.Drawing.Point(605, 629);
			this.buttonMarbles.Name = "buttonMarbles";
			this.buttonMarbles.Size = new System.Drawing.Size(32, 32);
			this.buttonMarbles.TabIndex = 23;
			this.buttonMarbles.UseVisualStyleBackColor = true;
			this.buttonMarbles.Click += new System.EventHandler(this.buttonMarbles_Click);
			// 
			// buttonDominos
			// 
			this.buttonDominos.Image = global::code.Resource.dominos;
			this.buttonDominos.Location = new System.Drawing.Point(644, 629);
			this.buttonDominos.Name = "buttonDominos";
			this.buttonDominos.Size = new System.Drawing.Size(32, 32);
			this.buttonDominos.TabIndex = 24;
			this.buttonDominos.UseVisualStyleBackColor = true;
			this.buttonDominos.Click += new System.EventHandler(this.buttonDominos_Click);
			// 
			// MainForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(873, 676);
			this.Controls.Add(this.buttonDominos);
			this.Controls.Add(this.buttonMarbles);
			this.Controls.Add(this.buttonBowling);
			this.Controls.Add(this.buttonSeaSaw);
			this.Controls.Add(this.checkBoxDebugDraw);
			this.Controls.Add(this.buttonShootBall);
			this.Controls.Add(this.buttonPrevStep);
			this.Controls.Add(this.buttonStep);
			this.Controls.Add(this.checkBoxRun);
			this.Controls.Add(this.textBoxWallY);
			this.Controls.Add(this.textBoxWallX);
			this.Controls.Add(this.buttonWall);
			this.Controls.Add(this.textBoxStack);
			this.Controls.Add(this.buttonStack);
			this.Controls.Add(this.checkAddShape);
			this.Controls.Add(this.buttonCyclinder);
			this.Controls.Add(this.buttonCircle);
			this.Controls.Add(this.buttonLine);
			this.Controls.Add(this.buttonRectPlane);
			this.Controls.Add(this.buttonPoint);
			this.Controls.Add(this.buttonSphere);
			this.Controls.Add(this.buttonFootball);
			this.Controls.Add(this.buttonCube);
			this.Controls.Add(this.buttonEllipsoid);
			this.Controls.Add(this.pictureBox);
			this.Name = "MainForm";
			this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Show;
			this.Text = "Support Mapping";
			((System.ComponentModel.ISupportInitialize)(this.pictureBox)).EndInit();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.PictureBox pictureBox;
		private System.Windows.Forms.Button buttonEllipsoid;
		private System.Windows.Forms.Button buttonCube;
		private System.Windows.Forms.Button buttonFootball;
		private System.Windows.Forms.Button buttonSphere;
		private System.Windows.Forms.Button buttonPoint;
		private System.Windows.Forms.Button buttonRectPlane;
		private System.Windows.Forms.Button buttonLine;
		private System.Windows.Forms.Button buttonCircle;
		private System.Windows.Forms.Button buttonCyclinder;
		private System.Windows.Forms.CheckBox checkAddShape;
		private System.Windows.Forms.Button buttonStack;
		private System.Windows.Forms.TextBox textBoxStack;
		private System.Windows.Forms.Button buttonWall;
		private System.Windows.Forms.TextBox textBoxWallX;
		private System.Windows.Forms.TextBox textBoxWallY;
		private System.Windows.Forms.CheckBox checkBoxRun;
		private System.Windows.Forms.Button buttonStep;
		private System.Windows.Forms.Button buttonPrevStep;
		private System.Windows.Forms.Button buttonShootBall;
		private System.Windows.Forms.CheckBox checkBoxDebugDraw;
		private System.Windows.Forms.Button buttonSeaSaw;
		private System.Windows.Forms.Button buttonBowling;
		private System.Windows.Forms.Button buttonMarbles;
		private System.Windows.Forms.Button buttonDominos;
	}
}

